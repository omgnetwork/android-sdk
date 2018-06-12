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
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.enum.SocketSystemEvent
import okhttp3.Response
import okhttp3.WebSocketListener

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
         * A socketChannel is used to receive some event for further handling in the [SocketChannel]
         */
        val socketChannel: SocketChannel?

        /**
         * A main thread executor used for change the background thread to the main thread before invoking the listener.
         */
        val mainThreadExecutor: MainThreadExecutor

        /**
         * A connectionListener will be passed to the [systemEventDispatcher] for further handling.
         */
        var connectionListener: SocketConnectionListener?
    }

    interface SystemEventDispatcher {
        /**
         * A connection listener that will be used for dispatch the [SocketConnectionListener] events.
         */
        var socketConnectionListener: SocketConnectionListener?

        /**
         * A channel listener that be used for dispatch the [SocketChannelListener] events.
         */
        var socketChannelListener: SocketChannelListener?

        /**
         * The web socket replied object from eWallet API.
         */
        var socketReceive: SocketReceive?

        /**
         * A socketChannel for delegate the event to the [SocketChannel] internally for further handling the event.
         */
        var socketChannel: SocketChannel?

        /**
         * Handles the [SocketSystemEvent] and may dispatch the [SocketChannelListener] or [SocketConnectionListener] to the client.
         *
         * @param systemEvent To indicate which event of the [SocketSystemEvent]
         */
        fun handleEvent(systemEvent: SocketSystemEvent)

        /**
         * the Websocket's [onFailure] will be delegated to this function
         *
         * @see [WebSocketListener]
         */
        fun handleSocketFailure(throwable: Throwable, response: Response?)

        /**
         * the Websocket's [onOpened] will be delegated to this function
         *
         * @see [WebSocketListener]
         */
        fun handleSocketOpened(response: Response)

        /**
         * the Websocket's [onClosed] will be delegated to this function
         *
         * @see [WebSocketListener]
         */
        fun handleSocketClosed(code: Int, reason: String)
    }

    interface CustomEventDispatcher {
        /**
         * For dispatching the [SocketCustomEventListener] event.
         */
        val customEventListenerMap: MutableMap<String, SocketCustomEventListener>

        /**
         * For dispatching the [SocketChannelListener] event.
         */
        var socketChannelListener: SocketChannelListener?

        /**
         * The web socket replied object from eWallet API.
         */
        var socketReceive: SocketReceive?

        /**
         * Clear all callbacks in the customEventListenerMap
         */
        fun clearCustomEventListenerMap()

        /**
         * Handles the [SocketCustomEvent] and dispatch the [SocketCustomEventListener] to the client.
         *
         * @param customEvent To indicate the actual type of generic [SocketCustomEvent]
         */
        fun handleEvent(customEvent: SocketCustomEvent)

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

    /* Channel Package */
    interface SocketChannel {
        /**
         * Executes when the client have been left the channel successfully.
         *
         * @param topic A topic indicating which channel will be joined.
         */
        fun onLeftChannel(topic: String)

        /**
         * Executes when the client have been joined the channel successfully.
         *
         * @param topic A topic indicating which channel will be joined.
         */
        fun onJoinedChannel(topic: String)

        /**
         * Returns a boolean indicating if the channel is joined.
         *
         * @param topic A topic indicating which channel will be joined.
         * @return A boolean indicating if the channel is joined.
         */
        fun joined(topic: String): Boolean

        fun onSocketOpened()
    }
}
