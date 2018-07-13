package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.constant.HTTPHeaders
import co.omisego.omisego.custom.retrofit2.executor.MainThreadExecutor
import co.omisego.omisego.model.ClientConfiguration
import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.utils.GsonProvider
import co.omisego.omisego.utils.OMGEncryption
import co.omisego.omisego.websocket.SocketClientContract.Channel
import co.omisego.omisego.websocket.channel.SocketChannel
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.SocketMessageRef
import co.omisego.omisego.websocket.channel.dispatcher.CustomEventDispatcher
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcher
import co.omisego.omisego.websocket.channel.dispatcher.SystemEventDispatcher
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketDelegator
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketReceiveParser
import co.omisego.omisego.websocket.enum.SocketStatusCode
import co.omisego.omisego.websocket.interval.SocketHeartbeat
import co.omisego.omisego.websocket.listener.CompositeSocketChannelListener
import co.omisego.omisego.websocket.listener.CompositeSocketConnectionListener
import co.omisego.omisego.websocket.listener.SocketChannelListener
import co.omisego.omisego.websocket.listener.SocketChannelListenerSet
import co.omisego.omisego.websocket.listener.SocketConnectionListener
import co.omisego.omisego.websocket.listener.SocketConnectionListenerSet
import co.omisego.omisego.websocket.listener.SocketCustomEventListener
import co.omisego.omisego.websocket.listener.SocketCustomEventListenerSet
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.Executor

@Suppress("OVERRIDE_BY_INLINE")
/**
 * The [OMGSocketClient] represents an object that knows how to interact with the eWallet Web Socket API.
 * An instance should be created by using [OMGSocketClient.Builder]. For example,
 *
 * <code>
 *     val config = ClientConfiguration(
 *          baseURL = "YOUR_BASE_URL",
 *          apiKey = "YOUR_API_KEY",
 *          authenticationToken = "YOUR_AUTH_TOKEN"
 *      )
 *
 *     val omgSocketClient = OMGSocketClient.Builder{
 *          clientConfiguration = config
 *          debug = false
 *     }.build()
 * </code>
 *
 * The available methods and details are listed in the [SocketClientContract.Client]
 *
 * @see SocketClientContract.Client
 */
class OMGSocketClient internal constructor(
    internal val okHttpClient: OkHttpClient,
    internal var request: Request,
    internal val socketSendParser: SocketClientContract.PayloadSendParser,
    internal val webSocketListenerProvider: WebSocketListenerProvider
) : SocketClientContract.Client, SocketChannelContract.SocketClient,
    SocketConnectionListenerSet,
    SocketChannelListenerSet,
    SocketCustomEventListenerSet {

    internal var wsClient: WebSocket? = null
    override lateinit var socketChannel: SocketClientContract.Channel

    /**
     * A [SocketHeartbeat] is responsible for schedule sending the heartbeat event for keep the connection alive
     */
    override val socketHeartbeat: SocketClientContract.SocketInterval by lazy {
        SocketHeartbeat(SocketMessageRef(scheme = SocketMessageRef.SCHEME_HEARTBEAT))
    }

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
        socketChannel.hasSentAllPendingJoinChannel() && (wsClient?.queueSize() ?: 0L) == 0L

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
    override fun joinChannel(
        topic: SocketTopic,
        payload: Map<String, Any>
    ) {
        socketChannel.join(topic.name, payload)
    }

    /**
     * Leave the [Channel] with the given [SocketTopic].
     * If the channel haven't joined yet, then this method does nothing.
     *
     * @param topic The topic (channel) to be left.
     * @param payload (Optional) the additional data you might want to send bundled with the request.
     */
    override fun leaveChannel(topic: SocketTopic, payload: Map<String, Any>) {
        socketChannel.leave(topic.name, payload)
    }

    /**
     * Set new authentication header. This will send leave request for every channel to the server.
     * You'll need to join the channel again (please wait for disconnected listener is invoked).
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

    @Deprecated(
        message = "Use \'addConnectionListener\' or \'removeConnectionListener\' instead",
        level = DeprecationLevel.ERROR
    )
    override fun setConnectionListener(connectionListener: SocketConnectionListener?) {}

    @Deprecated(
        "Use \'addChannelListener\' or \'removeChannelListener\' instead",
        level = DeprecationLevel.ERROR
    )
    override fun setChannelListener(channelListener: SocketChannelListener?) {}

    /**
     * Add listener for subscribing to the [SocketConnectionListener] event.
     *
     * @param connectionListener The [SocketConnectionListener] to be invoked when the channel has been joined, left, or got an error.
     * @see SocketConnectionListener for the event detail.
     */
    override fun addConnectionListener(connectionListener: SocketConnectionListener) {
        socketChannel.addConnectionListener(connectionListener)
    }

    /**
     * Remove the listener for unsubscribing from the [SocketConnectionListener] event.
     *
     * @param connectionListener The [SocketConnectionListener] to be unsubscribed.
     */
    override fun removeConnectionListener(connectionListener: SocketConnectionListener) {
        socketChannel.removeConnectionListener(connectionListener)
    }

    /**
     * Subscribe to the [SocketChannelListener] event.
     *
     * @param channelListener The [SocketChannelListener] to be invoked when the channel has been joined, left, or got an error.
     * @see SocketChannelListener for the event detail.
     */
    override fun addChannelListener(channelListener: SocketChannelListener) {
        socketChannel.addChannelListener(channelListener)
    }

    /**
     * Remove the listener for unsubscribing from the [SocketChannelListener] event.
     *
     * @param channelListener The [SocketChannelListener] to be unsubscribed.
     */
    override fun removeChannelListener(channelListener: SocketChannelListener) {
        socketChannel.removeChannelListener(channelListener)
    }

    override fun addCustomEventListener(customEventListener: SocketCustomEventListener) {
        socketChannel.addCustomEventListener(customEventListener)
    }

    override fun removeCustomEventListener(customEventListener: SocketCustomEventListener) {
        socketChannel.removeCustomEventListener(customEventListener)
    }

    /**
     * Send the [SocketSend] request to the eWallet web socket API.
     *
     * @return A boolean indicating if the message was sent successfully.
     */
    override fun send(message: SocketSend): Boolean {
        wsClient = wsClient ?: okHttpClient.newWebSocket(request, webSocketListenerProvider.webSocketListener)
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
         * (Required) A client configuration that need to be first initialized before calling build()
         */
        override var clientConfiguration: ClientConfiguration? = null

        /**
         * (Optional) A boolean indicating if debug info should be printed in the console. Default: false.
         */
        override var debug: Boolean = false

        /**
         * (Optional) An executor used for invoking the callback. Default to MainThreadExecutor.
         */
        override var executor: Executor = MainThreadExecutor()

        /**
         * Create a [OMGSocketClient] instance to be used for connecting to the web socket API.
         *
         * @return An instance of the [OMGSocketClient].
         * @throws IllegalStateException if [authenticationToken], [apiKey] or the [baseURL] is empty.
         */
        override fun build(): SocketClientContract.Client {
            val (baseURL, apiKey, authenticationToken) = clientConfiguration
                ?: throw IllegalStateException(Exceptions.MSG_NULL_CLIENT_CONFIGURATION)

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

            val compositeSocketConnectionListener = CompositeSocketConnectionListener()
            val compositeSocketChannelListener = CompositeSocketChannelListener()

            /* To invoke the socket channel listener */
            val systemEventDispatcher = SystemEventDispatcher(compositeSocketChannelListener)
            val customEventDispatcher = CustomEventDispatcher(compositeSocketChannelListener)

            /* To invoke the socket connection listener */
            val socketDispatcher = SocketDispatcher(
                systemEventDispatcher,
                customEventDispatcher,
                compositeSocketConnectionListener,
                executor
            )
            val socketDelegator = SocketDelegator(SocketReceiveParser(gson), socketDispatcher)

            val socketClient = OMGSocketClient(
                okHttpClient,
                request,
                SocketSendParser(gson),
                socketDelegator
            )

            // To add callbacks to the set
            val socketChannel = SocketChannel(
                socketDispatcher,
                socketClient,
                compositeSocketConnectionListener,
                compositeSocketChannelListener
            )
            socketClient talksTo socketChannel

            socketClient.wsClient = null

            return socketClient
        }

        init {
            init()
        }
    }
}

internal infix fun OMGSocketClient.talksTo(socketChannel: SocketClientContract.Channel) {
    this.socketChannel = socketChannel
}
