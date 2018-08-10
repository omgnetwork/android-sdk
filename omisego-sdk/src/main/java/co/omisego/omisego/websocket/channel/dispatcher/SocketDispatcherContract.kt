package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.enum.SocketSystemEvent
import co.omisego.omisego.websocket.listener.SocketChannelListener
import co.omisego.omisego.websocket.listener.SocketConnectionListener
import co.omisego.omisego.websocket.listener.SocketCustomEventListener
import java.util.concurrent.Executor

interface SocketDispatcherContract {

    /* Dispatcher Package */
    interface Dispatcher {

        /**
         * A systemEventDispatcher is responsible for handling the [SocketSystemEvent] and dispatch the [SocketConnectionListener] or the [SocketChannelListener].
         */
        val systemEventDispatcher: SystemEventDispatcher

        /**
         * A customEventDispatcher is responsible for handling the [SocketSystemEvent] and dispatch the [SocketCustomEventListener].
         */
        val customEventDispatcher: CustomEventDispatcher

        /**
         * A connectionListener will be passed to the [systemEventDispatcher] for further handling.
         */
        val connectionListener: SocketConnectionListener

        /**
         * An executor used when invoking the listener.
         */
        val executor: Executor
    }

    interface SystemEventDispatcher {

        /**
         * A channel listener that be used for dispatch the [SocketChannelListener] events.
         */
        val socketChannelListener: SocketChannelListener

        /**
         * Handles the [SocketSystemEvent] and may dispatch the [SocketChannelListener] or [SocketConnectionListener] to the client.
         *
         * @param systemEvent To indicate which event of the [SocketSystemEvent]
         */
        fun handleEvent(systemEvent: SocketSystemEvent, response: SocketReceive<*>)
    }

    interface CustomEventDispatcher {
        /**
         * For dispatching the [SocketChannelListener] event.
         */
        val socketChannelListener: SocketChannelListener

        /**
         * For dispatching the [SocketCustomEventListener] event.
         */
        val customEventListeners: MutableSet<SocketCustomEventListener>

        /**
         * Handles the [SocketCustomEvent] and dispatch the [SocketCustomEventListener] to the client.
         *
         * @param customEvent To indicate the actual type of generic [SocketCustomEvent]
         * @param response The websocket payload that is sent from the websocket API
         */
        fun handleEvent(customEvent: SocketCustomEvent, response: SocketReceive<*>)
    }
}
