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
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.enum.SocketSystemEvent
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
        fun handleEvent(systemEvent: SocketSystemEvent, response: SocketReceive)
    }

    interface CustomEventDispatcher {
        /**
         * For dispatching the [SocketCustomEventListener] event.
         */
        val customEventListenerMap: MutableMap<String, SocketCustomEventListener>

        /**
         * For dispatching the [SocketChannelListener] event.
         */
        val socketChannelListener: SocketChannelListener

        /**
         * Handles the [SocketCustomEvent] and dispatch the [SocketCustomEventListener] to the client.
         *
         * @param customEvent To indicate the actual type of generic [SocketCustomEvent]
         */
        fun handleEvent(customEvent: SocketCustomEvent, response: SocketReceive)

        /**
         * Handles the [SocketCustomEvent] event and dispatch the [SocketCustomEventListener.TransactionRequestListener].
         * This method will be invoked by the [handleEvent] method.
         *
         * @param socketReceive The web socket replied object from eWallet API.
         * @param customEvent The custom event used for decide the event to be dispatched
         */
        fun SocketCustomEventListener.TransactionRequestListener.handleTransactionRequestEvent(
            socketReceive: SocketReceive,
            customEvent: SocketCustomEvent
        )

        /**
         * Handles the [SocketCustomEvent] event and dispatch the [SocketCustomEventListener.TransactionConsumptionListener].
         * This method will be invoked by the [handleEvent] method.
         *
         * @param socketReceive The web socket replied object from eWallet API.
         * @param customEvent The custom event used for decide the event to be dispatched
         */
        fun SocketCustomEventListener.TransactionConsumptionListener.handleTransactionConsumptionEvent(
            socketReceive: SocketReceive,
            customEvent: SocketCustomEvent
        )

        /**
         * Handles the [SocketCustomEvent] event and dispatch the [SocketCustomEventListener.AnyEventListener].
         * This method will be invoked by the [handleEvent] method.
         *
         * @param socketReceive The web socket replied object from eWallet API.
         */
        fun SocketCustomEventListener.AnyEventListener.handleAnyEvent(
            socketReceive: SocketReceive
        )
    }
}
