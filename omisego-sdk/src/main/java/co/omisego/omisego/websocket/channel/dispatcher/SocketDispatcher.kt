package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.custom.retrofit2.executor.MainThreadExecutor
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.websocket.SocketChannelCallback
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketCustomEventCallback
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketDelegatorContract
import co.omisego.omisego.websocket.enum.SocketStatusCode
import okhttp3.Response
import okhttp3.WebSocketListener

class SocketDispatcher(
    override val socketDelegator: SocketDispatcherContract.Delegator,
    override val systemEventDispatcher: SocketDispatcherContract.SystemEventDispatcher,
    override val customEventDispatcher: SocketDispatcherContract.CustomEventDispatcher
) : SocketChannelContract.Dispatcher, SocketDispatcherContract.Dispatcher, SocketDelegatorContract.Dispatcher {
    override var socketChannel: SocketDispatcherContract.SocketChannel? = null
    override var socketConnectionListener: SocketConnectionCallback? = null
    override val mainThreadExecutor by lazy { MainThreadExecutor() }

    override fun setSocketConnectionCallback(connectionListener: SocketConnectionCallback?) {
        socketConnectionListener = connectionListener
        systemEventDispatcher.socketConnectionCallback = connectionListener
    }

    override fun setSocketChannelCallback(channelListener: SocketChannelCallback?) {
        systemEventDispatcher.socketChannelCallback = channelListener
        customEventDispatcher.socketChannelCallback = channelListener
    }

    override fun setSocketTransactionCallback(listener: SocketCustomEventCallback?) {
        customEventDispatcher.socketCustomEventCallback = listener
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
            customEventDispatcher.socketReceive = response
            response.event.either(systemEventDispatcher::handleEvent, customEventDispatcher::handleEvent)
        }
    }

    override fun dispatchOnFailure(throwable: Throwable, response: Response?) {
        socketConnectionListener?.onDisconnected(throwable)
    }
}

internal infix fun SocketDispatcher.talksTo(socketChannel: SocketDispatcherContract.SocketChannel) {
    this.socketChannel = socketChannel
}
