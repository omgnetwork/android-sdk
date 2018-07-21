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
import co.omisego.omisego.websocket.listener.ListenableTopicListener
import co.omisego.omisego.websocket.listener.SocketCustomEventListener
import co.omisego.omisego.websocket.listener.TransactionConsumptionTopicListener
import co.omisego.omisego.websocket.listener.TransactionRequestTopicListener
import co.omisego.omisego.websocket.strategy.FilterStrategy

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

@Deprecated(
    level = DeprecationLevel.ERROR,
    message = "Use startListeningEvents(SocketClientContract.Client, Map<String, Any>, TransactionRequestTopicListener) instead."
)
fun TransactionRequest.startListeningEvents(
    client: SocketClientContract.Client,
    payload: Map<String, Any> = mapOf(),
    listener: SocketCustomEventListener.TransactionRequestListener
) {
    with(client) {
        listener.strategy = FilterStrategy.Topic(socketTopic)
        addCustomEventListener(listener)
        joinChannel(socketTopic, payload)
    }
}

@Deprecated(
    level = DeprecationLevel.ERROR,
    message = "Use startListeningEvents(SocketClientContract.Client, Map<String, Any>, TransactionConsumptionTopicListener) instead."
)
fun TransactionConsumption.startListeningEvents(
    client: SocketClientContract.Client,
    payload: Map<String, Any> = mapOf(),
    listener: SocketCustomEventListener.TransactionConsumptionListener
) {
    with(client) {
        addCustomEventListener(listener)
        joinChannel(socketTopic, payload)
    }
}

@Deprecated(
    level = DeprecationLevel.ERROR,
    message = "Use startListeningEvents(SocketClientContract.Client, Map<String, Any>, ListenableTopicListener) instead."
)
fun User.startListeningEvents(
    client: SocketClientContract.Client,
    payload: Map<String, Any> = mapOf(),
    listener: SocketCustomEventListener
) {
    with(client) {
        listener.strategy = FilterStrategy.Topic(socketTopic)
        addCustomEventListener(listener)
        joinChannel(socketTopic, payload)
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
    listener: TransactionRequestTopicListener
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
    listener: TransactionConsumptionTopicListener
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
    listener: ListenableTopicListener
) {
    with(client) {
        addCustomEventListener(listener)
        joinChannel(socketTopic, payload)
    }
}
