package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import com.google.gson.Gson
import okhttp3.WebSocketListener

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
         * An authenticationToken used to tell the identity of who is connecting to the web socket API.
         */
        var authenticationToken: String

        /**
         * The base url of the eWallet server
         * This url must follow the web socket protocol (ws or wss for ssl).
         * The interface of the eWallet web socket API is available at `/api/socket`.
         * For example, ws(s)://ewallet.demo.omisego.io/api/socket
         */
        var baseURL: String

        /**
         * A boolean indicating if debug info should be printed in the console.
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
     * 1. The client will be automatically connected, once you've joined the channel.
     * 2. The client will be automatically disconnected, once you've left all the channels you've joined.
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
         * For example, if you are sending the topic begins with "transaction_request", then the listener must be the [SocketCustomEventCallback.TransactionRequestCallback] event.
         *
         * @see SocketCustomEventCallback
         */
        fun joinChannel(
            topic: SocketTopic,
            payload: Map<String, Any> = mapOf(),
            listener: SocketCustomEventCallback
        )

        /**
         * Leave the [Channel] with the given [SocketTopic].
         * If the channel haven't joined yet, then this method does nothing.
         *
         * @param topic The topic (channel) to be left.
         * @param payload (Optional) the additional data you might want to send bundled with the request.
         */
        fun leaveChannel(topic: SocketTopic, payload: Map<String, Any>)

        /**
         * Subscribe to the [SocketConnectionCallback] event.
         *
         * @param connectionListener The [SocketConnectionCallback] to be invoked when the web socket connection is connected or disconnected.
         * @see SocketConnectionCallback for the event detail.
         */
        fun setConnectionListener(connectionListener: SocketConnectionCallback)

        /**
         * Subscribe to the [SocketChannelCallback] event.
         *
         * @param channelListener The [SocketChannelCallback] to be invoked when the channel has been joined, left, or got an error.
         * @see SocketChannelCallback for the event detail.
         */
        fun setChannelListener(channelListener: SocketChannelCallback)
    }

    interface PayloadSendParser {
        val gson: Gson
        fun parse(payload: SocketSend): String
    }

    /* Channel Package */
    interface Channel {
        /**
         * Send [SocketEventSend.JOIN] event to the server. Do nothing if the channel has already joined.
         *
         * @param topic Join the channel by the given topic.
         * @param payload (Optional) the additional data you might want to send bundled with the request.
         */
        fun join(topic: SocketTopic, payload: Map<String, Any>)

        /**
         * Send [SocketEventSend.LEAVE] event to the server. Do nothing if the channel has already left.
         *
         * @param topic Leave from the channel by the given topic.
         * @param payload (Optional) payload you want to send along with the request/.
         */
        fun leave(topic: SocketTopic, payload: Map<String, Any>)

        /**
         * Retrieves a set of active [SocketTopic].
         *
         * @return A set of active [SocketTopic].
         */
        fun retrieveChannels(): Set<SocketTopic>

        /**
         * Retrieves the [WebSocketListener]
         *
         * @return [WebSocketListener]
         */
        fun retrieveWebSocketListener(): WebSocketListener

        /**
         * Subscribe to the [SocketConnectionCallback] event.
         *
         * @param connectionListener The [SocketConnectionCallback] to be invoked when the web socket connection is connected or disconnected
         */
        fun setConnectionListener(connectionListener: SocketConnectionCallback?)

        /**
         * Subscribe to the [SocketChannelCallback] event.
         *
         * @param channelListener The [SocketChannelCallback] to be invoked when the web socket channel has been joined, left or got an error.
         */
        fun setChannelListener(channelListener: SocketChannelCallback?)

        /**
         * Subscribe to the [SocketCustomEventCallback] event.
         *
         * @param customEventListener The [SocketCustomEventCallback] to be invoked when the [CustomEvent] event happened.
         *
         * @see SocketTransactionRequestEvent
         */
        fun setCustomEventListener(customEventListener: SocketCustomEventCallback?)
    }
}
