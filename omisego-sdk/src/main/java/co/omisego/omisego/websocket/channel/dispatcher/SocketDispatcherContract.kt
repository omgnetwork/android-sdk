package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.custom.retrofit2.executor.MainThreadExecutor
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.SocketChannelCallback
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketCustomEventCallback
import co.omisego.omisego.websocket.channel.SocketChannelContract.SocketClient
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.enum.SocketSystemEvent
import okhttp3.WebSocketListener

interface SocketDispatcherContract {

    /* Dispatcher Package */
    interface Dispatcher {
        /**
         * A socketDelegator is responsible for delegate a raw [WebSocketListener] event to be processed in the [Dispatcher].
         */
        val socketDelegator: Delegator

        /**
         * A systemEventDispatcher is responsible for handling the [SocketSystemEvent] and dispatch the [SocketConnectionCallback] or the [SocketChannelCallback].
         */
        val systemEventDispatcher: SystemEventDispatcher

        /**
         * A customEventDispatcher is responsible for handling the [SocketSystemEvent] and dispatch the [SocketCustomEventCallback].
         */
        val customEventDispatcher: CustomEventDispatcher

        /**
         * A socketChannel is used to receive some event for further handling in the [SocketChannel]
         */
        val socketChannel: SocketChannel?

        /**
         * A main thread executor used for change the background thread to the main thread before invoking the callback.
         */
        val mainThreadExecutor: MainThreadExecutor

        /**
         * A socketConnectionListener will be passed to the [systemEventDispatcher] for further handling.
         */
        var socketConnectionListener: SocketConnectionCallback?
    }

    interface SystemEventDispatcher {
        /**
         * A connection callback that will be used for dispatch the [SocketConnectionCallback] events.
         */
        var socketConnectionCallback: SocketConnectionCallback?

        /**
         * A channel callback that be used for dispatch the [SocketChannelCallback] events.
         */
        var socketChannelCallback: SocketChannelCallback?

        /**
         * The web socket replied object from eWallet API.
         */
        var socketReceive: SocketReceive?

        /**
         * A socketChannel for delegate the event to the [SocketChannel] internally for further handling the event.
         */
        var socketChannel: SocketChannel?

        /**
         * Handles the [SocketSystemEvent] and may dispatch the [SocketChannelCallback] or [SocketConnectionCallback] to the client.
         *
         * @param systemEvent To indicate which event of the [SocketSystemEvent]
         */
        fun handleEvent(systemEvent: SocketSystemEvent)
    }

    interface CustomEventDispatcher {
        /**
         * For dispatching the [SocketCustomEventCallback] event.
         */
        var socketCustomEventCallback: SocketCustomEventCallback?

        /**
         * For dispatching the [SocketChannelCallback] event.
         */
        var socketChannelCallback: SocketChannelCallback?

        /**
         * The web socket replied object from eWallet API.
         */
        var socketReceive: SocketReceive?

        /**
         * Handles the [SocketCustomEvent] and dispatch the [SocketCustomEventCallback] to the client.
         *
         * @param customEvent To indicate the actual type of generic [SocketCustomEvent]
         */
        fun handleEvent(customEvent: SocketCustomEvent)

        /**
         * Handles the [SocketCustomEvent] event and dispatch the [SocketCustomEventCallback.TransactionRequestCallback].
         * This method will be invoked by the [handleEvent] method.
         *
         * @param socketReceive The web socket replied object from eWallet API.
         * @param customEvent The custom event used for decide the event to be dispatched
         */
        fun SocketCustomEventCallback.TransactionRequestCallback.handleTransactionRequestEvent(
            socketReceive: SocketReceive,
            customEvent: SocketCustomEvent
        )

        /**
         * Handles the [SocketCustomEvent] event and dispatch the [SocketCustomEventCallback.TransactionConsumptionCallback].
         * This method will be invoked by the [handleEvent] method.
         *
         * @param socketReceive The web socket replied object from eWallet API.
         * @param customEvent The custom event used for decide the event to be dispatched
         */
        fun SocketCustomEventCallback.TransactionConsumptionCallback.handleTransactionConsumptionEvent(
            socketReceive: SocketReceive,
            customEvent: SocketCustomEvent
        )

        /**
         * Handles the [SocketCustomEvent] event and dispatch the [SocketCustomEventCallback.AnyEventCallback].
         * This method will be invoked by the [handleEvent] method.
         *
         * @param socketReceive The web socket replied object from eWallet API.
         */
        fun SocketCustomEventCallback.AnyEventCallback.handleAnyEvent(
            socketReceive: SocketReceive
        )
    }

    /* Delegator Package */
    interface Delegator {

        /**
         * Retrieves the [WebSocketListener]  to be used for initializing the [Websocket] in the [SocketClient].
         *
         * @return [WebSocketListener]
         */
        fun retrievesWebSocketListener(): WebSocketListener
    }

    /* Channel Package */
    interface SocketChannel {
        /**
         * Executes when the client have been left the channel successfully.
         *
         * @param topic A topic indicating which channel will be joined.
         */
        fun onLeftChannel(topic: SocketTopic)

        /**
         * Executes when the client have been joined the channel successfully.
         *
         * @param topic A topic indicating which channel will be joined.
         */
        fun onJoinedChannel(topic: SocketTopic)

        /**
         * Returns a boolean indicating if the channel is joined.
         *
         * @param topic A topic indicating which channel will be joined.
         * @return A boolean indicating if the channel is joined.
         */
        fun joined(topic: SocketTopic): Boolean
    }
}
