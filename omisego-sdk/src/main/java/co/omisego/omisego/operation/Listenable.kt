package co.omisego.omisego.operation

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 16/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.User
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.websocket.SocketClientContract
import co.omisego.omisego.websocket.SocketCustomEventListener
import co.omisego.omisego.websocket.SocketCustomEventListener.TransactionRequestListener
import co.omisego.omisego.websocket.SocketCustomEventListener.TransactionConsumptionListener

/**
 * Represents an object that can be listened with websocket
 */
interface Listenable<T : SocketCustomEventListener> {
    val socketTopic: SocketTopic<T>

    /**
     * Stop listening for events
     *
     * @param client The client used when starting to listen
     */
    fun stopListening(client: SocketClientContract.Client) {
        client.leaveChannel(socketTopic, mapOf())
    }
}

/**
 * Opens a websocket connection with the server and starts to listen for events happening on this transaction request.
 * Typically, this should be used to listen for consumption request made on the request.
 *
 * @param client The correctly initialised client to use for the websocket connection.
 * @param payload The additional metadata for the consumption
 * @param callback The delegate that will receive events.
 */
fun TransactionRequest.startListeningEvents(
    client: SocketClientContract.Client,
    payload: Map<String, Any> = mapOf(),
    callback: TransactionRequestListener
) {
    client.joinChannel(socketTopic, payload, callback)
}

/**
 * Opens a websocket connection with the server and starts to listen for events happening on this transaction consumption.
 * Typically, this should be used to listen for consumption confirmation.
 *
 * @param client The correctly initialised client to use for the websocket connection.
 * @param payload The additional metadata for the consumption
 * @param callback The delegate that will receive events.
 */
fun TransactionConsumption.startListeningEvents(
    client: SocketClientContract.Client,
    payload: Map<String, Any> = mapOf(),
    callback: TransactionConsumptionListener
) {
    client.joinChannel(socketTopic, payload, callback)
}

/**
 * Opens a websocket connection with the server and starts to listen for any event regarding the current user.
 *
 * @param client The correctly initialised client to use for the websocket connection.
 * @param payload The additional metadata for the consumption
 * @param callback The delegate that will receive events.
 */
fun <T : SocketCustomEventListener> User.startListeningEvents(
    client: SocketClientContract.Client,
    payload: Map<String, Any> = mapOf(),
    callback: T
) {
    client.joinChannel(socketTopic, payload, callback)
}
