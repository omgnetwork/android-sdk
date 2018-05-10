package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.util.Log
import co.omisego.omisego.custom.retrofit2.executor.MainThreadExecutor
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.socket.SocketReceiveData
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketTopicCallback
import co.omisego.omisego.websocket.SocketTransactionEvent
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketDelegatorContract
import co.omisego.omisego.websocket.enum.SocketEventReceive
import okhttp3.Response
import okhttp3.WebSocketListener

class SocketDispatcher(
    override val socketDelegator: SocketDispatcherContract.Delegator
) : SocketChannelContract.Dispatcher, SocketDispatcherContract.Core, SocketDelegatorContract.Dispatcher {
    override var socketChannel: SocketDispatcherContract.SocketChannel? = null
    override var socketConnectionCallback: SocketConnectionCallback? = null
    override var socketTopicCallback: SocketTopicCallback? = null
    override var socketTransactionEvent: SocketTransactionEvent? = null
    override val mainThreadExecutor by lazy { MainThreadExecutor() }

    override fun retrieveWebSocketListener(): WebSocketListener {
        return socketDelegator.getWebSocketListener()
    }

    override fun setCallbacks(
        socketConnectionCallback: SocketConnectionCallback?,
        socketTopicCallback: SocketTopicCallback?,
        socketTransactionEvent: SocketTransactionEvent?
    ) {
        this.socketConnectionCallback = socketConnectionCallback
        this.socketTopicCallback = socketTopicCallback
        this.socketTransactionEvent = socketTransactionEvent
    }

    override fun dispatchOnOpened(response: Response) {
        mainThreadExecutor.execute {
            socketConnectionCallback?.onConnected()
        }
    }

    override fun dispatchOnClosed(code: Int, reason: String) {
        mainThreadExecutor.execute {
            socketConnectionCallback?.onDisconnected()
        }
    }

    override fun dispatchOnMessage(response: SocketReceive) {
        Log.d("SocketDispatcher", "Dispatch OnMessage $response")
        mainThreadExecutor.execute {
            when (response.event) {
                SocketEventReceive.CLOSE -> {
                    socketChannel?.onLeftChannel(response.topic)
                    socketTopicCallback?.onUnsubscribedTopic()

                }
                SocketEventReceive.REPLY -> {
                    if (socketChannel?.joined(response.topic) != true) {
                        socketChannel?.onJoinedChannel(response.topic)
                        socketTopicCallback?.onSubscribedTopic()
                    }
                }
                SocketEventReceive.ERROR -> Log.d("SocketDispatcher", "Receive an error event.")
            }

            // Cannot do smart-cast if don't store in the immutable variable
            val transactionEvent = socketTransactionEvent

            if(response.event == SocketEventReceive.TRANSACTION_CONSUMPTION_REQUEST || response.event == SocketEventReceive.TRANSACTION_CONSUMPTION_FINALIZED) {
                when (transactionEvent) {
                    is SocketTransactionEvent.RequestEvent -> transactionEvent.handleTransactionRequestEvent(response)
                    is SocketTransactionEvent.ConsumptionEvent -> transactionEvent.handleTransactionConsumptionEvent(response)
                }
            }
        }
    }

    override fun SocketTransactionEvent.RequestEvent.handleTransactionRequestEvent(socketReceive: SocketReceive) {
        val error = socketReceive.error
        val finalizedEvent = socketReceive.event == SocketEventReceive.TRANSACTION_CONSUMPTION_FINALIZED
        val requestEvent = socketReceive.event == SocketEventReceive.TRANSACTION_CONSUMPTION_REQUEST
        val socketReceiveData = socketReceive.data

        when {
            requestEvent && error == null && socketReceiveData is SocketReceiveData.SocketConsumeTransaction -> {
                onTransactionConsumptionRequest(socketReceiveData.data)
            }
            finalizedEvent && error == null && socketReceiveData is SocketReceiveData.SocketConsumeTransaction -> {
                onTransactionConsumptionFinalizedSuccess(socketReceiveData.data)
            }
            finalizedEvent && error != null && socketReceiveData is SocketReceiveData.SocketConsumeTransaction -> {
                onTransactionConsumptionFinalizedFail(socketReceiveData.data, error)
            }
            error != null -> {
                socketTopicCallback?.onError(error)
            }
        }
    }

    override fun SocketTransactionEvent.ConsumptionEvent.handleTransactionConsumptionEvent(socketReceive: SocketReceive) {
        val error = socketReceive.error
        val finalizedEvent = socketReceive.event == SocketEventReceive.TRANSACTION_CONSUMPTION_FINALIZED
        val socketReceiveData = socketReceive.data

        when {
            finalizedEvent && error == null && socketReceiveData is SocketReceiveData.SocketConsumeTransaction -> {
                onTransactionConsumptionFinalizedSuccess(socketReceiveData.data)
            }
            finalizedEvent && error != null && socketReceiveData is SocketReceiveData.SocketConsumeTransaction -> {
                onTransactionConsumptionFinalizedFail(socketReceiveData.data, error)
            }
            error != null -> {
                socketTopicCallback?.onError(error)
            }
        }
    }

    override fun dispatchOnFailure(throwable: Throwable, response: Response?) {
        Log.e("SocketDispatcher", throwable.message)
    }
}