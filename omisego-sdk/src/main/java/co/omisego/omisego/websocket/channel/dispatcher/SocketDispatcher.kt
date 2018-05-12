package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.custom.retrofit2.executor.MainThreadExecutor
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketListenEvent
import co.omisego.omisego.websocket.SocketTopicCallback
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketDelegatorContract
import co.omisego.omisego.websocket.enum.SocketStatusCode
import okhttp3.Response
import okhttp3.WebSocketListener

class SocketDispatcher(
    override val socketDelegator: SocketDispatcherContract.Delegator,
    override val systemEventDispatcher: SocketDispatcherContract.SystemEventDispatcher,
    override val sendableEventDispatcher: SocketDispatcherContract.SendableEventDispatcher
) : SocketChannelContract.Dispatcher, SocketDispatcherContract.Core, SocketDelegatorContract.Dispatcher {
    override var socketChannel: SocketDispatcherContract.SocketChannel? = null
    override var socketConnectionListener: SocketConnectionCallback? = null
    override val mainThreadExecutor by lazy { MainThreadExecutor() }

    override fun setSocketConnectionCallback(connectionListener: SocketConnectionCallback?) {
        socketConnectionListener = connectionListener
        systemEventDispatcher.socketConnectionCallback = connectionListener
    }

    override fun setSocketTopicCallback(topicListener: SocketTopicCallback?) {
        systemEventDispatcher.socketTopicCallback = topicListener
        sendableEventDispatcher.socketTopicCallback = topicListener
    }

    override fun setSocketTransactionCallback(listener: SocketListenEvent?) {
        sendableEventDispatcher.socketListenEvent = listener
    }

    override fun retrieveWebSocketListener(): WebSocketListener {
        return socketDelegator.getWebSocketListener()
    }

    override fun dispatchOnOpened(response: Response) {
        mainThreadExecutor.execute {
            socketConnectionListener?.onConnected()
        }
    }

    override fun dispatchOnClosed(code: Int, reason: String) {
        mainThreadExecutor.execute {
            if (code == SocketStatusCode.NORMAL.code)
                socketConnectionListener?.onDisconnected(null)
            else {
                socketConnectionListener?.onDisconnected(Throwable("$code $reason"))
            }
        }
    }

    override fun dispatchOnMessage(response: SocketReceive) {
        mainThreadExecutor.execute {
            systemEventDispatcher.socketReceive = response
            sendableEventDispatcher.socketReceive = response
            response.event.either(systemEventDispatcher::handleEvent, sendableEventDispatcher::handleEvent)
        }
    }

    override fun dispatchOnFailure(throwable: Throwable, response: Response?) {
        socketConnectionListener?.onDisconnected(throwable)
    }
}

internal infix fun SocketDispatcher.talksTo(socketChannel: SocketDispatcherContract.SocketChannel) {
    this.socketChannel = socketChannel
}
