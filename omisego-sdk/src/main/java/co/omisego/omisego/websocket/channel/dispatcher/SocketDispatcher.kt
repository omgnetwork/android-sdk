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
import co.omisego.omisego.websocket.SocketChannelCallback
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketCustomEventCallback
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcherContract.Dispatcher
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcherContract.SocketChannel
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketDelegatorContract
import co.omisego.omisego.websocket.enum.SocketStatusCode
import co.omisego.omisego.websocket.enum.SocketSystemEvent
import okhttp3.Response
import okhttp3.WebSocketListener
import java.net.SocketException

/**
 * A callback dispatcher for events related to the web socket.
 *
 * @param socketDelegator responsible for delegate value raw [WebSocketListener] event to be processed in the [Dispatcher].
 * @param systemEventDispatcher responsible for handling the [SocketSystemEvent] and dispatch the [SocketConnectionCallback] or the [SocketChannelCallback]
 * @param customEventDispatcher responsible for handling the [SocketSystemEvent] and dispatch the [SocketCustomEventCallback].
 */
class SocketDispatcher(
    override val socketDelegator: SocketDispatcherContract.Delegator,
    override val systemEventDispatcher: SocketDispatcherContract.SystemEventDispatcher,
    override val customEventDispatcher: SocketDispatcherContract.CustomEventDispatcher
) : SocketChannelContract.Dispatcher, SocketDispatcherContract.Dispatcher, SocketDelegatorContract.Dispatcher {

    /**
     * A socketConnectionListener will be passed to the [systemEventDispatcher] for further handling.
     */
    override var socketConnectionListener: SocketConnectionCallback? = null

    /**
     * A main thread executor used for change the background thread to the main thread before invoking the callback.
     */
    override val mainThreadExecutor by lazy { MainThreadExecutor() }

    /**
     * A socketChannel is used to receive some event for further handling in the [SocketChannel]
     */
    override var socketChannel: SocketDispatcherContract.SocketChannel? = null
        set(value) {
            systemEventDispatcher.socketChannel = value
            field = value
        }

    /**
     * Set the socket connection callback to be used for dispatch the connection status event.
     */
    override fun setSocketConnectionCallback(connectionListener: SocketConnectionCallback?) {
        socketConnectionListener = connectionListener
        systemEventDispatcher.socketConnectionCallback = connectionListener
    }

    /**
     * Set the socket channel callback to be used for dispatch the channel status event.
     */
    override fun setSocketChannelCallback(channelListener: SocketChannelCallback?) {
        systemEventDispatcher.socketChannelCallback = channelListener
        customEventDispatcher.socketChannelCallback = channelListener
    }

    /**
     * Set the socket custom events callback to be used for dispatch the custom events.
     */
    override fun setSocketCustomEventCallback(customEventListener: SocketCustomEventCallback?) {
        customEventDispatcher.socketCustomEventCallback = customEventListener
    }

    /**
     * Retrieves the [WebSocketListener] to be used for initializing the [Websocket] in the [SocketClient].
     */
    override fun retrieveWebSocketListener(): WebSocketListener {
        return socketDelegator.retrievesWebSocketListener()
    }

    /**
     * Invoked when the method [WebSocketListener]'s onOpen is called.
     *
     * @param response The response from the OkHttp's WebSocket.
     */
    override fun dispatchOnOpened(response: Response) {
        mainThreadExecutor.execute {
            socketConnectionListener?.onConnected()
        }
    }

    /**
     * Invoked when the method [WebSocketListener]'s onClosed is called.
     *
     * @param code the status code explaining why the connection is being closed.
     * @param reason A human-readable string explaining why the connection is closing.
     */
    override fun dispatchOnClosed(code: Int, reason: String) {
        mainThreadExecutor.execute {
            if (code == SocketStatusCode.NORMAL.code)
                socketConnectionListener?.onDisconnected(null)
            else {
                socketConnectionListener?.onDisconnected(SocketException("$code $reason"))
            }
        }
    }

    /**
     * Invoked when the method [WebSocketListener]'s onMessage is called.
     *
     * @param response A [SocketReceive] object to be used for further handling by the [Dispatcher]
     */
    override fun dispatchOnMessage(response: SocketReceive) {
        Log.d("Zeus", response.toString())
        mainThreadExecutor.execute {
            when {
                response.event.isLeft -> systemEventDispatcher.socketReceive = response
                else -> customEventDispatcher.socketReceive = response
            }
            response.event.either(systemEventDispatcher::handleEvent, customEventDispatcher::handleEvent)
        }
    }

    /**
     * Invoked when the method [WebSocketListener]'s onFailure is called.
     *
     * @param throwable An exception is delegated from the [WebSocketListener]'s onFailure
     * @param response A response is delegated by the [WebSocketListener]'s onFailure
     */
    override fun dispatchOnFailure(throwable: Throwable, response: Response?) {
        socketConnectionListener?.onDisconnected(throwable)
    }
}

internal infix fun SocketDispatcher.talksTo(socketChannel: SocketDispatcherContract.SocketChannel) {
    this.socketChannel = socketChannel
}
