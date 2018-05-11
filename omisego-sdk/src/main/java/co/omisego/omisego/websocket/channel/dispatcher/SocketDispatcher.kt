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
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.socket.runIfNotInternalTopic
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
                    val topic = SocketTopic(response.topic)
                    topic.runIfNotInternalTopic {
                        socketChannel?.onLeftChannel(topic)
                        socketTopicCallback?.onUnSubscribedTopic(topic)
                    }
                }
                SocketEventReceive.REPLY -> {
                    val topic = SocketTopic(response.topic)
                    topic.runIfNotInternalTopic {
                        topic.runIfFirstJoined {
                            socketChannel?.onJoinedChannel(topic)
                            socketTopicCallback?.onSubscribedTopic(topic)
                        }
                    }
                }
                SocketEventReceive.ERROR -> Log.d("SocketDispatcher", "Receive an error event.")
            }

            // Cannot do smart-cast if don't store in the immutable variable
            val transactionEvent = socketTransactionEvent

            if (response.event == SocketEventReceive.TRANSACTION_CONSUMPTION_REQUEST || response.event == SocketEventReceive.TRANSACTION_CONSUMPTION_FINALIZED) {
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

    /**
     * Run the lambda when meets the following condition
     *  - The topic hasn't joined yet
     */
    internal inline fun SocketTopic.runIfFirstJoined(lambda: () -> Unit) {
        if (socketChannel?.joined(this) == false) {
            lambda()
        }
    }
}

infix fun SocketDispatcher.talksTo(socketChannel: SocketDispatcherContract.SocketChannel) {
    this.socketChannel = socketChannel
}