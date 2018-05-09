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
import co.omisego.omisego.utils.GsonProvider
import co.omisego.omisego.websocket.channel.SocketChannel
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcher
import co.omisego.omisego.websocket.channel.dispatcher.callback.SocketDelegator
import co.omisego.omisego.websocket.channel.dispatcher.callback.SocketReceiveParser
import co.omisego.omisego.websocket.enum.SocketStatusCode
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.logging.HttpLoggingInterceptor

class SocketClient internal constructor(
    internal val okHttpClient: OkHttpClient,
    internal val request: Request,
    internal val socketSendParser: SocketClientContract.PayloadSendParser
) : SocketClientContract.Core, SocketChannelContract.SocketClient {
    internal var wsClient: WebSocket? = null
    internal lateinit var socketChannel: SocketClientContract.Channel
    override var socketConnectionCallback: SocketConnectionCallback? = null
    override var socketTopicCallback: SocketTopicCallback? = null
    override var socketTransactionRequestEvent: SocketTransactionRequestEvent? = null

    /* SocketClientContract.Core */
    /**
     * Immediately and violently release resources held by this web socket, discarding any enqueued
     * messages. This does nothing if the web socket has already been closed or canceled.
     */
    override fun cancel() {
        wsClient?.cancel()
    }

    override fun hasSentAllMessages(): Boolean =
        (wsClient?.queueSize() ?: 0L) == 0L

    override fun joinChannel(topic: String) {
        socketChannel.addChannel(topic)
    }

    override fun leaveChannel(topic: String) {
        socketChannel.removeChannel(topic)
    }

    override fun setConnectionCallback(callback: SocketConnectionCallback) {
        socketConnectionCallback = callback
        invalidateCallbacks()
    }

    override fun setTopicCallback(callback: SocketTopicCallback) {
        socketTopicCallback = callback
        invalidateCallbacks()
    }

    override fun setTransactionRequestEventCallback(callback: SocketTransactionRequestEvent) {
        socketTransactionRequestEvent = callback
        invalidateCallbacks()
    }

    /* SocketClientContract.SocketClient */

    override fun send(message: SocketSend): Boolean {
        wsClient = wsClient ?: okHttpClient.newWebSocket(request, socketChannel.retrieveWebSocketListener())
        val payload = socketSendParser.parse(message)
        return wsClient?.send(payload) ?: false
    }

    override fun closeConnection(status: SocketStatusCode, reason: String) {
        wsClient?.close(status.code, reason)
    }

    private fun invalidateCallbacks() {
        socketChannel.setCallbacks(socketConnectionCallback, socketTopicCallback, socketTransactionRequestEvent)
    }

    class Builder(init: Builder.() -> Unit) : SocketClientContract.Builder {
        override var authenticationToken: String = ""
            set(value) {
                if (value.isEmpty()) throw Exceptions.emptyAuthenticationToken
                field = value
            }

        override var baseURL: String = ""
            set(value) {
                if (value.isEmpty()) throw Exceptions.emptyBaseURL
                field = value
            }

        override var debug: Boolean = false

        override fun build(): SocketClientContract.Core {
            when {
                authenticationToken.isEmpty() -> throw Exceptions.emptyAuthenticationToken
                baseURL.isEmpty() -> throw Exceptions.emptyBaseURL
            }

            val request = Request.Builder()
                .url(baseURL)
                .addHeader(HTTPHeaders.AUTHORIZATION, "${HTTPHeaders.AUTHORIZATION_SCHEME} $authenticationToken")
                .addHeader(HTTPHeaders.ACCEPT, HTTPHeaders.ACCEPT_OMG)
                .build()

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

            /* Bind two-way communication. SocketDispatcher <--> SocketDelegator */
            val socketDispatcher = SocketDispatcher(socketDelegator)
            socketDelegator.socketDispatcher = socketDispatcher

            /* Bind two-way communication. SocketClient <--> SocketChannel */
            val socketChannel = SocketChannel(socketDispatcher, socketClient)
            socketClient.socketChannel = socketChannel
            socketDispatcher.socketChannel = socketChannel

            socketClient.wsClient = null

            /* The connection flow will be look like SocketClient <--> SocketChannel <--> SocketDispatcher <--> SocketDelegator */

            return socketClient
        }

        init {
            init()
        }
    }
}
