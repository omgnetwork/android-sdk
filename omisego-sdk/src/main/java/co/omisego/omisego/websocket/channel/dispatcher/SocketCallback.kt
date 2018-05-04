package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class SocketCallback : SocketDispatcherContract.Callback {
    private val listener: WebSocketListener by lazy {
        object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket?, response: Response?) {
                super.onOpen(webSocket, response)
            }

            override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
                super.onFailure(webSocket, t, response)
            }

            override fun onMessage(webSocket: WebSocket?, text: String?) {
                super.onMessage(webSocket, text)
            }

            override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
                super.onClosed(webSocket, code, reason)
            }
        }
    }

    override fun getWebSocketListener(): WebSocketListener {
        return listener
    }
}