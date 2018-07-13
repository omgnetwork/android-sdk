package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.listener.CompositeSocketChannelListener
import co.omisego.omisego.websocket.listener.CompositeSocketConnectionListener
import co.omisego.omisego.websocket.SocketClientContract
import co.omisego.omisego.websocket.enum.SocketStatusCode
import co.omisego.omisego.websocket.listener.SocketCustomEventListenerSet
import java.util.concurrent.BlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

interface SocketChannelContract {
    /* Channel Package */
    interface Channel {
        val pendingChannelsQueue: BlockingQueue<SocketSend>

        /**
         * A boolean indicating channels are currently leaving or not
         */
        val leavingChannels: AtomicBoolean

        /**
         * A [Dispatcher] is responsible dispatch all events to the client.
         */
        val socketDispatcher: Dispatcher

        /**
         * A [SocketClient] for sending a message to the eWallet web socket API and close the web socket connection.
         */
        val socketClient: SocketClient

        val compositeSocketConnectionListener: CompositeSocketConnectionListener
        val compositeSocketChannelListener: CompositeSocketChannelListener

        /**
         * A [SocketMessageRef] is responsible for create unique ref value to be included in the [SocketSend] request.
         */
        val socketMessageRef: SocketChannelContract.MessageRef

        /**
         * Create a [SocketSend] instance to be used for join a channel.
         *
         * @param topic A topic indicating which channel will be joined.
         * @param payload (Optional) the additional data you might want to send bundled with the request.
         *
         * @return A [SocketSend] instance used for joining the channel.
         */
        fun createJoinMessage(topic: String, payload: Map<String, Any>): SocketSend

        /**
         * Create a [SocketSend] instance to be used for join a channel.
         *
         * @param topic A topic indicating which channel will be joined.
         * @param payload (Optional) the additional data you might want to send bundled with the request.
         *
         * @return A [SocketSend] instance used for leaving the channel.
         */
        fun createLeaveMessage(topic: String, payload: Map<String, Any>): SocketSend

        /**
         * Check if we're currently leaving all channels or not.
         *
         * @return true, if we're currently leaving all channels, otherwise false.
         */
        fun joinable(): Boolean
    }

    /**
     * The [MessageRef] is responsible for create a unique string to be used for including in the [SocketSend].
     */
    interface MessageRef {
        val scheme: String
        val value: String
    }

    /* WebSocket Package */
    interface SocketClient {

        /**
         * A [SocketHeartbeat] is responsible for schedule sending the heartbeat event for keep the connection alive
         */
        val socketHeartbeat: SocketClientContract.SocketInterval

        /**
         * Send the [SocketSend] request to the eWallet web socket API.
         *
         * @return A boolean indicating if the message was sent successfully.
         */
        fun send(message: SocketSend): Boolean

        /**
         * Close the web socket connection
         *
         * @param status The web socket status code
         * @param reason The detail why the connection is closed
         */
        fun closeConnection(status: SocketStatusCode, reason: String)
    }

    /* Dispatcher Package */
    interface Dispatcher : SocketCustomEventListenerSet {
        /**
         * Clear all callbacks in the customEventListeners
         */
        fun clearCustomEventListeners()
    }
}
