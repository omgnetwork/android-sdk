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
 * The [SocketClient] is the main file used to connect to the eWallet web socket API.
 *
 * The available methods and details are listed in the [SocketClientContract.Client]
 * @see SocketClientContract.Client
 */
class SocketClient internal constructor(
    internal val okHttpClient: OkHttpClient,
    internal val request: Request,
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
     * Subscribe to the [SocketConnectionCallback] event.
     *
     * @param connectionListener The [SocketConnectionCallback] to be invoked when the web socket connection is connected or disconnected.
     * @see SocketConnectionCallback for the event detail.
     */
    override fun setConnectionListener(connectionListener: SocketConnectionCallback) {
        socketChannel.setConnectionListener(connectionListener)
    }

    /**
     * Subscribe to the [SocketChannelCallback] event.
     *
     * @param channelListener The [SocketChannelCallback] to be invoked when the channel has been joined, left, or got an error.
     * @see SocketChannelCallback for the event detail.
     */
    override fun setChannelListener(channelListener: SocketChannelCallback) {
        socketChannel.setChannelListener(channelListener)
    }

    override fun send(message: SocketSend): Boolean {
        wsClient = wsClient ?: okHttpClient.newWebSocket(request, socketChannel.retrieveWebSocketListener())
        val payload = socketSendParser.parse(message)
        return wsClient?.send(payload) ?: false
    }

    override fun closeConnection(status: SocketStatusCode, reason: String) {
        wsClient?.close(status.code, reason)
        wsClient = null
    }

    /**
     * A [SocketClient.Builder] used to define the required data to create an instance of the [SocketClient]
     */
    class Builder(init: Builder.() -> Unit) : SocketClientContract.Builder {
        /**
         * An authenticationToken used to tell the identity of who is connecting to the web socket API.
         */
        override var authenticationToken: String = ""
            set(value) {
                check(value.isNotEmpty()) { Exceptions.MSG_EMPTY_AUTH_TOKEN }
                field = value
            }

        /**
         * The base url of the eWallet server
         * This url must follow the web socket protocol (ws or wss for ssl).
         * The interface of the eWallet web socket API is available at `/api/socket`.
         * For example, ws(s)://ewallet.demo.omisego.io/api/socket
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

        override fun build(): SocketClientContract.Client {
            check(authenticationToken.isNotEmpty()) { Exceptions.MSG_EMPTY_AUTH_TOKEN }
            check(baseURL.isNotEmpty()) { Exceptions.MSG_EMPTY_BASE_URL }

            val request = Request.Builder().apply {
                url(baseURL)
                addHeader(HTTPHeaders.AUTHORIZATION, "${HTTPHeaders.AUTHORIZATION_SCHEME} $authenticationToken")
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

            val socketClient = SocketClient(
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

            /* The sdk's websocket flow will look like SocketClient <--> SocketChannel <--> SocketDispatcher <--> SocketDelegator */

            return socketClient
        }

        init {
            init()
        }
    }
}

infix fun SocketClient.talksTo(socketChannel: SocketClientContract.Channel) {
    this.socketChannel = socketChannel
}
