package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.ClientConfiguration
import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.SocketChannelContract.SocketClient
import co.omisego.omisego.websocket.channel.SocketMessageRef
import com.google.gson.Gson
import java.util.Timer

/**
 * The _Contracts.kt files can be found in the almost web socket's sub-packages.
 * They contain the interfaces for communicating between each other both place in the same package and the directed neighbor package(s).
 * They also have the naming convention that append word "Contract" to the main file's name.
 * Note: The main file are the only file that able talk to each other.
 */
interface SocketClientContract {
    /* WebSocket Package */

    /**
     * An interface for [SocketClient.Builder] to define the required data to create an instance of the [SocketClient]
     */
    interface Builder {

        /**
         * (Required) A client configuration that need to be first initialized before calling build()
         */
        var clientConfiguration: ClientConfiguration?

        /**
         * (Optional) A boolean indicating if debug info should be printed in the console. Default: false.
         */
        var debug: Boolean

        /**
         * Create a [SocketClient] instance to be used for connecting to the web socket API.
         *
         * @return An instance of the [SocketClient].
         * @throws IllegalStateException if either [authenticationToken] or the [baseURL] is empty.
         */
        fun build(): SocketClientContract.Client
    }

    /**
     * An interface for the [SocketClient].
     * This interface defines all methods that the client can be used.
     *
     * Note: As you can see, there is no method like `connect` or `disconnect` are provided. The reason is here:
     * 1. The client will be automatically connected before you've joined the first channel.
     * 2. The client will be automatically disconnected after you've left all the channels you've joined.
     */
    interface Client {
        /**
         * A [Channel] responsible for join and leave the web socket channel.
         */
        val socketChannel: Channel

        /**
         * Immediately and violently release resources held by this web socket, discarding any enqueued
         * messages. This does nothing if the web socket has already been closed or canceled.
         */
        fun cancel()

        /**
         * A boolean indicating if all messages have been sent to the server.
         *
         * @return true, if all messages have been sent, otherwise false.
         */
        fun hasSentAllMessages(): Boolean

        /**
         * Joining a channel by the given [SocketTopic].
         *
         * @param topic The topic (channel) to which the event to be sent.
         * @param payload (Optional) the additional data you might want to send bundled with the request.
         * @param listener The event you want to receive for the specified [Channel].
         * Be careful, the listener should be related to the topic, otherwise you won't receive any message.
         * For example, if you are sending the topic begins with "transaction_request", then the listener must be the [SocketCustomEventListener.TransactionRequestListener] event.
         *
         * @see SocketCustomEventListener
         */
        fun <T : SocketCustomEventListener> joinChannel(
            topic: SocketTopic<T>,
            payload: Map<String, Any> = mapOf(),
            listener: T
        )

        /**
         * Leave the [Channel] with the given [SocketTopic].
         * If the channel haven't joined yet, then this method does nothing.
         *
         * @param topic The topic (channel) to be left.
         * @param payload (Optional) the additional data you might want to send bundled with the request.
         */
        fun <T : SocketCustomEventListener> leaveChannel(topic: SocketTopic<T>, payload: Map<String, Any>)

        /**
         * Set new authentication header
         *
         * @param apiKey is the API key (typically generated on the admin panel).
         * @param authenticationToken is the token corresponding to an OmiseGO Wallet user retrievable using one of our server-side SDKs.
         */
        fun setAuthenticationHeader(apiKey: String, authenticationToken: String)

        /**
         * Set an interval of milliseconds for scheduling the interval event such as the heartbeat event which used for keeping the connection alive.
         *
         * @param period an interval of milliseconds
         */
        fun setIntervalPeriod(period: Long)

        /**
         * Subscribe to the [SocketConnectionListener] event.
         *
         * @param connectionListener The [SocketConnectionListener] to be invoked when the web socket connection is connected or disconnected.
         * @see SocketConnectionListener for the event detail.
         */
        fun setConnectionListener(connectionListener: SocketConnectionListener?)

        /**
         * Subscribe to the [SocketChannelListener] event.
         *
         * @param channelListener The [SocketChannelListener] to be invoked when the channel has been joined, left, or got an error.
         * @see SocketChannelListener for the event detail.
         */
        fun setChannelListener(channelListener: SocketChannelListener?)
    }

    interface PayloadSendParser {
        /**
         * A gson object for parsing the [SocketSend] to the json string.
         */
        val gson: Gson

        /**
         * Parse [SocketSend] object to raw json for sending to the web socket API.
         *
         * @param payload [SocketSend] object which will be sent to the eWallet web socket API.
         * @return A json string for sending to the web socket API.
         */
        fun parse(payload: SocketSend): String
    }

    /* Channel Package */
    interface Channel {
        /**
         * An interval of milliseconds for scheduling the interval event such as the heartbeat event which used for keeping the connection alive.
         * Default 5,000 milliseconds.
         */
        var period: Long

        /**
         * Send [SocketEventSend.JOIN] event to the server. Do nothing if the channel has already joined.
         *
         * @param topic Join the channel by the given topic.
         * @param payload (Optional) the additional data you might want to send bundled with the request.
         */
        fun join(topic: String, payload: Map<String, Any>)

        /**
         * Send [SocketEventSend.LEAVE] event to the server. Do nothing if the channel has already left.
         *
         * @param topic Leave from the channel by the given topic.
         * @param payload (Optional) payload you want to send along with the request/.
         */
        fun leave(topic: String, payload: Map<String, Any>)

        /**
         * Send leave event for all currently active channels.
         */
        fun leaveAll()

        /**
         * Retrieves a set of active [SocketTopic].
         *
         * @return A set of active [SocketTopic].
         */
        fun retrieveChannels(): Set<String>

        /**
         * Subscribe to the [SocketConnectionListener] event.
         *
         * @param connectionListener The [SocketConnectionListener] to be invoked when the web socket connection is connected or disconnected
         */
        fun setConnectionListener(connectionListener: SocketConnectionListener?)

        /**
         * Subscribe to the [SocketChannelListener] event.
         *
         * @param channelListener The [SocketChannelListener] to be invoked when the web socket channel has been joined, left or got an error.
         */
        fun setChannelListener(channelListener: SocketChannelListener?)

        /**
         * Subscribe to the [SocketCustomEventListener] event.
         *
         * @param customEventListener The [SocketCustomEventListener] to be invoked when the [CustomEvent] event happened.
         */
        fun setCustomEventListener(customEventListener: SocketCustomEventListener?)
    }

    /* Interval Package */
    interface SocketInterval {
        /**
         * The timer for scheduling the [SocketSend] periodically to be sent to the server.
         */
        var timer: Timer?

        /**
         * [SocketMessageRef] is responsible for creating a unique ref value to be sent with the [SocketSend].
         */
        val socketMessageRef: SocketChannelContract.MessageRef

        /**
         * An interval of milliseconds between the end of the previous task and the start of the next one.
         */
        var period: Long

        /**
         * Start to schedule the [SocketSend] to be sent to the server periodically.
         *
         * @param task A lambda with a [SocketSend] parameter. This will be executed periodically that starts immediately.
         */
        fun startInterval(task: (SocketSend) -> Unit)

        /**
         * Stop to schedule the task to be sent to the server.
         */
        fun stopInterval()
    }
}
