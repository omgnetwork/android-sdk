package co.omisego.omisego.websocket.channel.dispatcher.callback

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcherContract
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class SocketDelegator(
    override val socketResponseParser: SocketDelegatorContract.PayloadReceiveParser
) : SocketDispatcherContract.Delegator, SocketDelegatorContract.Core, WebSocketListener() {
    override var socketDispatcher: SocketDelegatorContract.Dispatcher? = null

    override fun onOpen(webSocket: WebSocket, response: Response) {
        socketDispatcher?.dispatchOnOpened(response)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        socketDispatcher?.dispatchOnFailure(t, response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val response = socketResponseParser.parse(text)
        socketDispatcher?.dispatchOnMessage(response)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        socketDispatcher?.dispatchOnClosed(code, reason)
    }

    override fun getWebSocketListener(): WebSocketListener {
        return this
    }
}