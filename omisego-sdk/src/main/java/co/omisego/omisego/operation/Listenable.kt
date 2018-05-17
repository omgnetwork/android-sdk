package co.omisego.omisego.operation

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 16/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.websocket.SocketClientContract
import co.omisego.omisego.websocket.SocketCustomEventCallback

/**
 * Represents an object that can be listened with websocket
 */
interface Listenable {
    val socketTopic: String

    /**
     * Stop listening for events
     *
     * @param client The client used when starting to listen
     */
    fun stopListening(client: SocketClientContract.Client) {
        client.leaveChannel(createSocketTopic(), mapOf())
    }
}

private fun Listenable.createSocketTopic(): SocketTopic {
    return SocketTopic(socketTopic)
}

/**
 * Opens a websocket connection with the server and starts to listen for events happening on this transaction request.
 * Typically, this should be used to listen for consumption request made on the request.
 *
 * @param client The correctly initialised client to use for the websocket connection.
 * @param callback The delegate that will receive events.
 */
fun TransactionRequest.startListeningEvents(client: SocketClientContract.Client, callback: SocketCustomEventCallback.TransactionRequestCallback) {
    client.joinChannel(createSocketTopic(), mapOf(), callback)
}

/**
 * Opens a websocket connection with the server and starts to listen for events happening on this transaction consumption.
 * Typically, this should be used to listen for consumption confirmation.
 *
 * @param client The correctly initialised client to use for the websocket connection.
 * @param callback The delegate that will receive events.
 */
fun TransactionConsumption.startListeningEvents(client: SocketClientContract.Client, callback: SocketCustomEventCallback.TransactionConsumptionCallback) {
    client.joinChannel(createSocketTopic(), mapOf(), callback)
}
