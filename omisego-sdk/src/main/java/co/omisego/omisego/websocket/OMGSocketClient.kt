package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.constant.HTTPHeaders
import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.utils.GsonProvider
import co.omisego.omisego.utils.OMGEncryption
import co.omisego.omisego.websocket.SocketClientContract.Channel
import co.omisego.omisego.websocket.channel.SocketChannel
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.dispatcher.CustomEventDispatcher
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcher
import co.omisego.omisego.websocket.channel.dispatcher.SystemEventDispatcher
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketDelegator
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketReceiveParser
import co.omisego.omisego.websocket.channel.dispatcher.delegator.talksTo
import co.omisego.omisego.websocket.channel.dispatcher.talksTo
import co.omisego.omisego.websocket.enum.SocketStatusCode
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.logging.HttpLoggingInterceptor

/**
 * The [OMGSocketClient] represents an object that knows how to interact with the eWallet Web Socket API.
 * An instance should be created by using [OMGSocketClient.Builder]. For example,
 *
 * <code>
 *     val omgSocketClient = OMGSocketClient.Builder{
 *          authenticationToken = YOUR_TOKEN
 *          apiKey = YOUR_API_KEY
 *          baseUrl = wss://your_ewallet_domain/api/socket/
 *     }.build(
 * </code>
 *
 * The available methods and details are listed in the [SocketClientContract.Client
 *
 * @see SocketClientContract.Client
 */
class OMGSocketClient internal constructor(
    internal val okHttpClient: OkHttpClient,
    internal var request: Request,
    internal val socketSendParser: SocketClientContract.PayloadSendParser
) : SocketClientContract.Client, SocketChannelContract.SocketClient {
    internal var wsClient: WebSocket? = null
    override lateinit var socketChannel: SocketClientContract.Channel

    /**
     * Immediately and violently release resources held by this web socket, discarding any enqueued
     * messages. This does nothing if the web socket has already been closed or canceled.
     */
    override fun cancel() {
        wsClient?.cancel()
    }

    /**
     * A boolean indicating if all messages have been sent to the server.
     *
     * @return true, if all messages have been sent, otherwise false.
     */
    override fun hasSentAllMessages(): Boolean =
        (wsClient?.queueSize() ?: 0L) == 0L

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
    override fun joinChannel(
        topic: SocketTopic,
        payload: Map<String, Any>,
        listener: SocketCustomEventCallback
    ) {
        with(socketChannel) {
            join(topic, payload)
            setCustomEventListener(listener)
        }
    }

    /**
     * Leave the [Channel] with the given [SocketTopic].
     * If the channel haven't joined yet, then this method does nothing.
     *
     * @param topic The topic (channel) to be left.
     * @param payload (Optional) the additional data you might want to send bundled with the request.
     */
    override fun leaveChannel(topic: SocketTopic, payload: Map<String, Any>) {
        socketChannel.leave(topic, payload)
    }

    /**
     * Set new authentication header. This will send leave request for every channel to the server.
     * You'll need to join the channel again (please wait for disconnected callback is invoked).
     *
     * @param apiKey is the API key (typically generated on the admin panel).
     * @param authenticationToken is the token corresponding to an OmiseGO Wallet user retrievable using one of our server-side SDKs.
     */
    override fun setAuthenticationHeader(apiKey: String, authenticationToken: String) {
        // Leave all channels
        socketChannel.leaveAll()

        // Create new request to use with a new authenticationHeader
        request = Request.Builder()
            .url(request.url())
            .addHeader(HTTPHeaders.AUTHORIZATION, "${HTTPHeaders.AUTHORIZATION_SCHEME} ${OMGEncryption.createAuthorizationHeader(apiKey, authenticationToken)}")
            .addHeader(HTTPHeaders.ACCEPT, HTTPHeaders.ACCEPT_OMG)
            .build()
    }

    /**
     * Set an interval of milliseconds for scheduling the interval event such as the heartbeat event which used for keeping the connection alive.
     *
     * @param period an interval of milliseconds (Default 5_000)
     */
    override fun setIntervalPeriod(period: Long) {
        socketChannel.period = period
    }

    /**
     * Subscribe to the [SocketConnectionCallback] event.
     *
     * @param connectionListener The [SocketConnectionCallback] to be invoked when the web socket connection is connected or disconnected.
     * @see SocketConnectionCallback for the event detail.
     */
    override fun setConnectionListener(connectionListener: SocketConnectionCallback?) {
        socketChannel.setConnectionListener(connectionListener)
    }

    /**
     * Subscribe to the [SocketChannelCallback] event.
     *
     * @param channelListener The [SocketChannelCallback] to be invoked when the channel has been joined, left, or got an error.
     * @see SocketChannelCallback for the event detail.
     */
    override fun setChannelListener(channelListener: SocketChannelCallback?) {
        socketChannel.setChannelListener(channelListener)
    }

    /**
     * Send the [SocketSend] request to the eWallet web socket API.
     *
     * @return A boolean indicating if the message was sent successfully.
     */
    override fun send(message: SocketSend): Boolean {
        wsClient = wsClient ?: okHttpClient.newWebSocket(request, socketChannel.retrieveWebSocketListener())
        val payload = socketSendParser.parse(message)
        return wsClient?.send(payload) ?: false
    }

    /**
     * Close the web socket connection
     *
     * @param status The web socket status code
     * @param reason The detail why the connection is closed
     */
    override fun closeConnection(status: SocketStatusCode, reason: String) {
        wsClient?.close(status.code, reason)
        wsClient = null
    }

    /**
     * A [OMGSocketClient.Builder] used to define the required data to create an instance of the [OMGSocketClient]
     */
    class Builder(init: Builder.() -> Unit) : SocketClientContract.Builder {
        /**
         * An authenticationToken is the token corresponding to an OmiseGO Wallet user retrievable using one of our server-side SDKs.
         *
         * @throws IllegalStateException if set with an empty string.
         */
        override var authenticationToken: String = ""
            set(value) {
                check(value.isNotEmpty()) { Exceptions.MSG_EMPTY_AUTH_TOKEN }
                field = value
            }

        /**
         *  An apiKey is the API key (typically generated on the admin panel)
         *
         * @throws IllegalStateException if set with an empty string.
         */
        override var apiKey: String = ""
            set(value) {
                check(value.isNotEmpty()) { Exceptions.MSG_EMPTY_API_KEY }
                field = value
            }

        /**
         * The base url of the eWallet server
         * This url must follow the web socket protocol (ws or wss for ssl).
         * The interface of the eWallet web socket API is available at `/api/socket`.
         * For example, ws(s)://ewallet.demo.omisego.io/api/socket
         *
         * @throws IllegalStateException if set with an empty string.
         */
        override var baseURL: String = ""
            set(value) {
                check(value.isNotEmpty()) { Exceptions.MSG_EMPTY_BASE_URL }
                field = value
            }

        /**
         * A boolean indicating if debug info should be printed in the console.
         */
        override var debug: Boolean = false

        /**
         * Create a [OMGSocketClient] instance to be used for connecting to the web socket API.
         *
         * @return An instance of the [OMGSocketClient].
         * @throws IllegalStateException if [authenticationToken], [apiKey] or the [baseURL] is empty.
         */
        override fun build(): SocketClientContract.Client {
            check(authenticationToken.isNotEmpty()) { Exceptions.MSG_EMPTY_AUTH_TOKEN }
            check(apiKey.isNotEmpty()) { Exceptions.MSG_EMPTY_API_KEY }
            check(baseURL.isNotEmpty()) { Exceptions.MSG_EMPTY_BASE_URL }

            val request = Request.Builder().apply {
                url(baseURL)
                addHeader(HTTPHeaders.AUTHORIZATION, "${HTTPHeaders.AUTHORIZATION_SCHEME} ${OMGEncryption.createAuthorizationHeader(apiKey, authenticationToken)}")
                addHeader(HTTPHeaders.ACCEPT, HTTPHeaders.ACCEPT_OMG)
            }.build()

            val okHttpClient = OkHttpClient.Builder().apply {
                /* If set debug true, then print the http logging */
                if (debug) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }.build()

            val gson = GsonProvider.create()

            val socketClient = OMGSocketClient(
                okHttpClient,
                request,
                SocketSendParser(gson)
            )

            val socketDelegator = SocketDelegator(SocketReceiveParser(gson))
            val socketDispatcher = SocketDispatcher(socketDelegator, SystemEventDispatcher(), CustomEventDispatcher())
            socketDelegator talksTo socketDispatcher

            val socketChannel = SocketChannel(socketDispatcher, socketClient)
            socketClient talksTo socketChannel
            socketDispatcher talksTo socketChannel

            socketClient.wsClient = null

            return socketClient
        }

        init {
            init()
        }
    }
}

infix fun OMGSocketClient.talksTo(socketChannel: SocketClientContract.Channel) {
    this.socketChannel = socketChannel
}
