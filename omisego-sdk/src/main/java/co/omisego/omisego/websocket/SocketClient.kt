package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.constant.HTTPHeaders
import co.omisego.omisego.websocket.channel.SocketChannel
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.SocketEventSend
import co.omisego.omisego.websocket.channel.SocketStatusCode
import co.omisego.omisego.websocket.channel.dispatcher.SocketCallback
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class SocketClient private constructor(
    private val okHttpClient: OkHttpClient,
    private val socketMessageRef: SocketMessageRef,
    private val request: Request
) : SocketClientContract.Core, SocketChannelContract.SocketClient {
    private var wsClient: WebSocket? = null
    private lateinit var socketChannel: SocketChannel

    override fun send(topic: String, event: SocketEventSend) {
        if (wsClient == null) {
            wsClient = okHttpClient.newWebSocket(request, socketChannel.retrieveWebSocketCallback())
        }
        wsClient?.send(topic)
    }

    override fun closeConnection(status: SocketStatusCode, reason: String) {
        wsClient?.close(status.code, reason)
        okHttpClient.dispatcher().executorService().shutdown()
    }

    override fun joinChannel(topic: String) {
        socketChannel.addChannel(topic)
    }

    override fun leaveChannel(topic: String) {
        socketChannel.removeChannel(topic)
    }

    /* The initializer of the [SocketClient] */
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

        override fun build(): SocketClient {
            when {
                authenticationToken.isEmpty() -> throw Exceptions.emptyAuthenticationToken
                baseURL.isEmpty() -> throw Exceptions.emptyBaseURL
            }

            val request = Request.Builder()
                .addHeader(HTTPHeaders.AUTHORIZATION, "${HTTPHeaders.AUTHORIZATION_SCHEME} $authenticationToken")
                .addHeader(HTTPHeaders.ACCEPT, HTTPHeaders.ACCEPT_OMG)
                .build()

            val okHttpClient = OkHttpClient()
            val socketMessageRef = SocketMessageRef()

            val socketClient = SocketClient(
                okHttpClient,
                socketMessageRef,
                request
            )

            val socketChannel = SocketChannel(
                SocketDispatcher(SocketCallback()),
                socketClient
            )

            socketClient.socketChannel = socketChannel
            socketClient.wsClient = null

            return socketClient
        }

        init {
            init()
        }
    }
}