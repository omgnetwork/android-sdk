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
import co.omisego.omisego.websocket.channel.SocketChannel
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.dispatcher.SystemEventDispatcher
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcher
import co.omisego.omisego.websocket.channel.dispatcher.SendableEventDispatcher
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketDelegator
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketReceiveParser
import co.omisego.omisego.websocket.channel.dispatcher.delegator.talksTo
import co.omisego.omisego.websocket.channel.dispatcher.talksTo
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
    override lateinit var socketChannel: SocketClientContract.Channel

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

    override fun joinChannel(
        topic: SocketTopic,
        payload: Map<String, Any>,
        listener: SocketListenEvent?
    ) {
        with(socketChannel) {
            addChannel(topic, payload)
            setSocketTransactionCallback(listener)
        }
    }

    override fun leaveChannel(topic: SocketTopic, payload: Map<String, Any>) {
        socketChannel.removeChannel(topic, payload)
    }

    override fun setConnectionListener(connectionListener: SocketConnectionCallback) {
        socketChannel.setSocketConnectionCallback(connectionListener)
    }

    override fun setTopicListener(topicListener: SocketTopicCallback) {
        socketChannel.setSocketTopicCallback(topicListener)
    }

    /* SocketClientContract.SocketClient */

    override fun send(message: SocketSend): Boolean {
        wsClient = wsClient ?: okHttpClient.newWebSocket(request, socketChannel.retrieveWebSocketListener())
        val payload = socketSendParser.parse(message)
        return wsClient?.send(payload) ?: false
    }

    override fun closeConnection(status: SocketStatusCode, reason: String) {
        wsClient?.close(status.code, reason)
        wsClient = null
    }

    class Builder(init: Builder.() -> Unit) : SocketClientContract.Builder {
        override var authenticationToken: String = ""
            set(value) {
                check(value.isNotEmpty()) { Exceptions.MSG_EMPTY_AUTH_TOKEN }
                field = value
            }

        override var baseURL: String = ""
            set(value) {
                check(value.isNotEmpty()) { Exceptions.MSG_EMPTY_BASE_URL }
                field = value
            }

        override var debug: Boolean = false

        override fun build(): SocketClientContract.Core {
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

            val socketDispatcher = SocketDispatcher(socketDelegator, SystemEventDispatcher(), SendableEventDispatcher())
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
