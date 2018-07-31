package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.enum.SocketStatusCode
import co.omisego.omisego.websocket.interval.SocketHeartbeat
import co.omisego.omisego.websocket.listener.internal.CompositeSocketChannelListener
import co.omisego.omisego.websocket.listener.internal.CompositeSocketConnectionListener
import co.omisego.omisego.websocket.listener.internal.SocketCustomEventListenerSet
import java.util.concurrent.BlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

interface SocketChannelContract {
    /* Channel Package */
    interface Channel {
        val socketSendCreator: SocketSendCreator
        val socketPendingChannel: SocketPendingChannel
        val compositeSocketConnectionListener: CompositeSocketConnectionListener
        val compositeSocketChannelListener: CompositeSocketChannelListener

        /**
         * [SocketReconnect] is responsible for re-initialized the websocket client and re-join all of the active channels.
         */
        val socketReconnect: SocketReconnect

        /**
         * A boolean indicating channels are currently leavingAllChannels or not
         */
        val leavingAllChannels: AtomicBoolean

        /**
         * A [Dispatcher] is responsible dispatch all events to the client.
         */
        val socketDispatcher: Dispatcher

        /**
         * A [SocketClient] for sending a message to the eWallet web socket API and close the web socket connection.
         */
        val socketClient: SocketClient

        /**
         * Retrieves a set of active [SocketTopic].
         *
         * @return A set of active [SocketTopic].
         */
        fun retrieveChannels(): Set<String>

        /**
         * Disconnect the websocket client.
         */
        fun disconnect(status: SocketStatusCode, reason: String)

        /**
         * Run socket heartbeat when the first channel is joined.
         */
        fun startHeartbeatWhenBegin()

        /**
         * Start reconnect mechanism when the socket connection has failed.
         */
        fun startReconnect(throwable: Throwable?)
    }

    interface SocketSendCreator {
        /**
         * A [SocketMessageRef] is responsible for create unique ref value to be included in the [SocketSend] request.
         */
        val socketMessageRef: SocketMessageRef

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
         * @return A [SocketSend] instance used for leavingAllChannels the channel.
         */
        fun createLeaveMessage(topic: String, payload: Map<String, Any>): SocketSend
    }

    interface PendingChannel {
        val pendingChannelsQueue: BlockingQueue<SocketSend>

        /**
         * Join all pending channels in the queue.
         */
        fun execute(join: (topic: String, payload: Map<String, Any>) -> Unit)

        /**
         * Add channel to pending channel queue.
         */
        fun add(socketSend: SocketSend, period: Long)

        /**
         * Remove channel from pending channel queue.
         */
        fun remove(topic: String)
    }

    interface SocketReconnect {
        fun add(socketSend: SocketSend)
        fun remove(topic: String)
        fun stopReconnectIfDone(channelSet: Set<String>)
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
        val socketHeartbeat: SocketHeartbeat

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
