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
import co.omisego.omisego.websocket.event.TransactionConsumptionListener
import co.omisego.omisego.websocket.event.TransactionRequestListener
import co.omisego.omisego.websocket.listener.SocketCustomEventListener

/**
 * Represents an object that can be listened with websocket
 */
interface Listenable {
    val socketTopic: SocketTopic

    /**
     * Stop listening for events
     *
     * @param client The client used when starting to listen
     * @param payload The additional metadata for leaving the channel.
     */
    fun stopListening(client: SocketClientContract.Client, payload: Map<String, Any> = mapOf()) {
        client.leaveChannel(socketTopic, payload)
    }
}

/**
 * Opens a websocket connection with the server and starts to listen for events happening on this transaction request.
 * Typically, this should be used to listen for consumption request made on the request.
 *
 * @param client The correctly initialised client to use for the websocket connection.
 * @param payload The additional metadata for the consumption
 * @param listener The delegate that will receive events.
 */
fun TransactionRequest.startListeningEvents(
    client: SocketClientContract.Client,
    payload: Map<String, Any> = mapOf(),
    listener: TransactionRequestListener
) {
    with(client) {
        addCustomEventListener(listener)
        joinChannel(socketTopic, payload)
    }
}

/**
 * Opens a websocket connection with the server and starts to listen for events happening on this transaction consumption.
 * Typically, this should be used to listen for consumption confirmation.
 *
 * @param client The correctly initialised client to use for the websocket connection.
 * @param payload The additional metadata for the consumption
 * @param listener The delegate that will receive events.
 */
fun TransactionConsumption.startListeningEvents(
    client: SocketClientContract.Client,
    payload: Map<String, Any> = mapOf(),
    listener: TransactionConsumptionListener
) {
    with(client) {
        addCustomEventListener(listener)
        joinChannel(socketTopic, payload)
    }
}

/**
 * Opens a websocket connection with the server and starts to listen for any event regarding the current user.
 *
 * @param client The correctly initialised client to use for the websocket connection.
 * @param payload The additional metadata for the consumption
 * @param listener The delegate that will receive events.
 */
fun User.startListeningEvents(
    client: SocketClientContract.Client,
    payload: Map<String, Any> = mapOf(),
    listener: SocketCustomEventListener
) {
    client.addCustomEventListener(listener)
    client.joinChannel(socketTopic, payload)
}
