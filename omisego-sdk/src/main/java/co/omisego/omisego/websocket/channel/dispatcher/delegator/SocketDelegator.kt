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
import co.omisego.omisego.websocket.SocketCustomEventListener
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcherContract
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * SocketDelegator is responsible for delegate the [WebSocketListener] events to the [SocketDelegatorContract.Dispatcher].
 *
 * @param socketResponseParser A socketResponseParser is responsible for parse a raw replied json object to the [SocketReceive] model.
 */
class SocketDelegator(
    override val socketResponseParser: SocketDelegatorContract.PayloadReceiveParser
) : SocketDispatcherContract.Delegator, SocketDelegatorContract.Delegator, WebSocketListener() {

    override var socketDispatcher: SocketDelegatorContract.Dispatcher? = null

    override fun onOpen(webSocket: WebSocket, response: Response) {
        socketDispatcher?.dispatchOnOpened(response)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        socketDispatcher?.dispatchOnFailure(t, response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val socketReceive = socketResponseParser.parse(text)
        SocketTopic<SocketCustomEventListener>(socketReceive.topic).runIfNotInternalTopic {
            socketDispatcher?.dispatchOnMessage(socketReceive)
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        socketDispatcher?.dispatchOnClosed(code, reason)
    }

    override fun retrievesWebSocketListener(): WebSocketListener {
        return this
    }
}

internal infix fun SocketDelegator.talksTo(socketDispatcher: SocketDelegatorContract.Dispatcher) {
    this.socketDispatcher = socketDispatcher
}
