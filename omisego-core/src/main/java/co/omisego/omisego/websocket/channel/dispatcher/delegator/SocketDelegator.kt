package co.omisego.omisego.websocket.channel.dispatcher.delegator

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.socket.runIfNotInternalTopic
import co.omisego.omisego.websocket.WebSocketListenerProvider
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * SocketDelegator is responsible for delegate the [WebSocketListener] events to the [SocketDelegatorContract.Dispatcher].
 *
 * @param socketResponseParser A socketResponseParser is responsible for parse a raw replied json object to the [SocketReceive] model.
 * @param socketDispatcher A socketDispatcher is responsible for dispatch an event which received from the server.
 */
class SocketDelegator(
    override val socketResponseParser: SocketDelegatorContract.PayloadReceiveParser,
    override val socketDispatcher: SocketDelegatorContract.Dispatcher
) : SocketDelegatorContract.Delegator,
    WebSocketListenerProvider,
    WebSocketListener() {

    override val webSocketListener: WebSocketListener
        get() = this

    override fun onOpen(webSocket: WebSocket, response: Response) {
        socketDispatcher.dispatchOnOpen(response)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        socketDispatcher.dispatchOnFailure(t, response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val socketReceive = socketResponseParser.parse(text)
        SocketTopic(socketReceive.topic).runIfNotInternalTopic {
            socketDispatcher.dispatchOnMessage(socketReceive)
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        socketDispatcher.dispatchOnClosed(code, reason)
    }
}
