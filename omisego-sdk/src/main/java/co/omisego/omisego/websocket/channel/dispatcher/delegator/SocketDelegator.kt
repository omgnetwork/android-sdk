package co.omisego.omisego.websocket.channel.dispatcher.delegator

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.websocket.channel.SocketChannelContract.SocketClient
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcherContract
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class SocketDelegator(
    /**
     * A socketResponseParser is responsible for parse a raw replied json object to the [SocketReceive] model.
     */
    override val socketResponseParser: SocketDelegatorContract.PayloadReceiveParser
) : SocketDispatcherContract.Delegator, SocketDelegatorContract.Delegator, WebSocketListener() {

    /**
     * A socketDispatcher is responsible for the further handling the raw response from the OkHttp's [WebSocketListener].
     */
    override var socketDispatcher: SocketDelegatorContract.Dispatcher? = null

    override fun onOpen(webSocket: WebSocket, response: Response) {
        socketDispatcher?.dispatchOnOpened(response)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        socketDispatcher?.dispatchOnFailure(t, response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val socketReceive = socketResponseParser.parse(text)
        socketDispatcher?.dispatchOnMessage(socketReceive)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        socketDispatcher?.dispatchOnClosed(code, reason)
    }

    /**
     * Retrieves the [WebSocketListener]  to be used for initializing the [Websocket] in the [SocketClient].
     *
     * @return [WebSocketListener]
     */
    override fun retrievesWebSocketListener(): WebSocketListener {
        return this
    }
}

infix fun SocketDelegator.talksTo(socketDispatcher: SocketDelegatorContract.Dispatcher) {
    this.socketDispatcher = socketDispatcher
}
