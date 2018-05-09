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
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketTopicCallback
import co.omisego.omisego.websocket.SocketTransactionRequestEvent
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.dispatcher.callback.SocketDelegatorContract
import co.omisego.omisego.websocket.enum.SocketEventReceive
import okhttp3.Response
import okhttp3.WebSocketListener

class SocketDispatcher(
    override val socketDelegator: SocketDispatcherContract.Delegator
) : SocketChannelContract.Dispatcher, SocketDispatcherContract.Core, SocketDelegatorContract.Dispatcher {
    override var socketConnectionCallback: SocketConnectionCallback? = null
    override var socketTopicCallback: SocketTopicCallback? = null
    override var socketTransactionRequestEvent: SocketTransactionRequestEvent? = null
    override var socketChannel: SocketDispatcherContract.SocketChannel? = null
    override val mainThreadExecutor by lazy { MainThreadExecutor() }

    override fun retrieveWebSocketListener(): WebSocketListener {
        return socketDelegator.getWebSocketListener()
    }

    override fun handleRequestEvents() {
        Log.d("SocketDispatcher", "Dispatch On")
    }

    override fun handleConsumeEvents() {
        Log.d("SocketDispatcher", "Dispatch On")
    }

    override fun setCallbacks(
        socketConnectionCallback: SocketConnectionCallback?,
        socketTopicCallback: SocketTopicCallback?,
        socketTransactionRequestEvent: SocketTransactionRequestEvent?
    ) {
        this.socketConnectionCallback = socketConnectionCallback
        this.socketTopicCallback = socketTopicCallback
        this.socketTransactionRequestEvent = socketTransactionRequestEvent
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
                    socketTopicCallback?.onSubscribedTopic()
                }
                SocketEventReceive.REPLY -> {
                    if (socketChannel?.joined(response.topic) != true) {
                        socketChannel?.onJoinedChannel(response.topic)
                        socketTopicCallback?.onUnsubscribedTopic()
                    }
                }
                SocketEventReceive.ERROR -> socketTransactionRequestEvent?.onTransactionConsumptionFailed()
                SocketEventReceive.TRANSACTION_CONSUMPTION_REQUEST -> socketTransactionRequestEvent?.onTransactionConsumptionRequest(response.data)
                SocketEventReceive.TRANSACTION_CONSUMPTION_FINALIZED -> socketTransactionRequestEvent?.onTransactionConsumptionSuccess(response.data)
                SocketEventReceive.OTHER -> TODO()
            }
        }
    }

    override fun dispatchOnFailure(throwable: Throwable, response: Response?) {
        Log.d("SocketDispatcher", "Dispatch OnFailure")
    }
}