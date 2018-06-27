package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.websocket.SocketChannelListener
import co.omisego.omisego.websocket.SocketConnectionListener
import co.omisego.omisego.websocket.SocketCustomEventListener
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketDelegatorContract
import co.omisego.omisego.websocket.enum.SocketStatusCode
import co.omisego.omisego.websocket.enum.SocketSystemEvent
import okhttp3.Response
import java.net.SocketException
import java.util.concurrent.Executor

/**
 * A listener dispatcher for events related to the web socket.
 * This class responsibility is mainly to split events between system events and user events.
 *
 * @param systemEventDispatcher responsible for handling the [SocketSystemEvent] and dispatch the [SocketConnectionListener] or the [SocketChannelListener]
 * @param customEventDispatcher responsible for handling the [SocketSystemEvent] and dispatch the [SocketCustomEventListener].
 * @param executor used to invoke listener
 */
class SocketDispatcher(
    override val systemEventDispatcher: SocketDispatcherContract.SystemEventDispatcher,
    override val customEventDispatcher: SocketDispatcherContract.CustomEventDispatcher,
    override val connectionListener: SocketConnectionListener,
    override val executor: Executor
) : SocketChannelContract.Dispatcher, SocketDispatcherContract.Dispatcher, SocketDelegatorContract.Dispatcher {

    override fun addCustomEventListener(customEventListener: SocketCustomEventListener) {
        customEventDispatcher.customEventListeners.add(customEventListener)
    }

    override fun removeCustomEventListener(customEventListener: SocketCustomEventListener) {
        customEventDispatcher.customEventListeners.remove(customEventListener)
    }

    override fun clearCustomEventListeners() {
        customEventDispatcher.customEventListeners.clear()
    }

    override fun dispatchOnOpen(response: Response) {
        executor.execute {
            connectionListener.onConnected()
        }
    }

    override fun dispatchOnClosed(code: Int, reason: String) {
        executor.execute {
            val exception =
                if (code == SocketStatusCode.NORMAL.code) {
                    null
                } else {
                    SocketException("$code $reason")
                }

            connectionListener.onDisconnected(exception)
        }
    }

    override fun dispatchOnMessage(response: SocketReceive<*>) {
        executor.execute {
            response.event.either(
                doOnLeft = { systemEventDispatcher.handleEvent(it, response) },
                doOnRight = { customEventDispatcher.handleEvent(it, response) }
            )
        }
    }

    override fun dispatchOnFailure(throwable: Throwable, response: Response?) {
        executor.execute {
            connectionListener.onDisconnected(throwable)
        }
    }
}
