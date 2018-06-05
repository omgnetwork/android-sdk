package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.custom.retrofit2.executor.MainThreadExecutor
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.websocket.SocketChannelListener
import co.omisego.omisego.websocket.SocketConnectionListener
import co.omisego.omisego.websocket.SocketCustomEventListener
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcherContract.Dispatcher
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketDelegatorContract
import co.omisego.omisego.websocket.enum.SocketStatusCode
import co.omisego.omisego.websocket.enum.SocketSystemEvent
import okhttp3.Response
import okhttp3.WebSocketListener
import java.net.SocketException

/**
 * A listener dispatcher for events related to the web socket.
 *
 * @param socketDelegator responsible for delegate value raw [WebSocketListener] event to be processed in the [Dispatcher].
 * @param systemEventDispatcher responsible for handling the [SocketSystemEvent] and dispatch the [SocketConnectionListener] or the [SocketChannelListener]
 * @param customEventDispatcher responsible for handling the [SocketSystemEvent] and dispatch the [SocketCustomEventListener].
 */
class SocketDispatcher(
    override val systemEventDispatcher: SocketDispatcherContract.SystemEventDispatcher,
    override val customEventDispatcher: SocketDispatcherContract.CustomEventDispatcher
) : SocketChannelContract.Dispatcher, SocketDispatcherContract.Dispatcher, SocketDelegatorContract.Dispatcher {

    override var connectionListener: SocketConnectionListener? = null

    override val mainThreadExecutor by lazy { MainThreadExecutor() }

    override var socketChannel: SocketDispatcherContract.SocketChannel? = null
        set(value) {
            systemEventDispatcher.socketChannel = value
            field = value
        }

    override fun setSocketConnectionListener(connectionListener: SocketConnectionListener?) {
        this.connectionListener = connectionListener
        systemEventDispatcher.socketConnectionListener = connectionListener
    }

    override fun setSocketChannelListener(channelListener: SocketChannelListener?) {
        systemEventDispatcher.socketChannelListener = channelListener
        customEventDispatcher.socketChannelListener = channelListener
    }

    override fun addCustomEventListener(topic: String, customEventListener: SocketCustomEventListener) {
        customEventDispatcher.customEventListenerMap[topic] = customEventListener
    }

    override fun dispatchOnOpen(response: Response) {
        mainThreadExecutor.execute {
            connectionListener?.onConnected()
        }
    }

    override fun dispatchOnClosed(code: Int, reason: String) {
        mainThreadExecutor.execute {
            customEventDispatcher.customEventListenerMap.clear()
            if (code == SocketStatusCode.NORMAL.code)
                connectionListener?.onDisconnected(null)
            else {
                connectionListener?.onDisconnected(SocketException("$code $reason"))
            }
        }
    }

    override fun dispatchOnMessage(response: SocketReceive) {
        mainThreadExecutor.execute {
            when {
                response.event.isLeft -> systemEventDispatcher.socketReceive = response
                else -> customEventDispatcher.socketReceive = response
            }
            response.event.either(systemEventDispatcher::handleEvent, customEventDispatcher::handleEvent)
        }
    }

    override fun dispatchOnFailure(throwable: Throwable, response: Response?) {
        connectionListener?.onDisconnected(throwable)
    }
}

internal infix fun SocketDispatcher.talksTo(socketChannel: SocketDispatcherContract.SocketChannel) {
    this.socketChannel = socketChannel
}
