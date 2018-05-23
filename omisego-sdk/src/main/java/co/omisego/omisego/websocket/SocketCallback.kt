package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 8/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption

/**
 * A web socket connection callback that executed when the web socket client is connected to the server or disconnected from the server.
 */
interface SocketConnectionCallback {
    /**
     * Invoked when the web socket client has connected to the eWallet web socket API successfully.
     */
    fun onConnected()

    /**
     * Invoked when the web socket client has disconnected from the eWallet web socket API.
     *
     * @param throwable (Optional) The exception might be raised if the web socket was not disconnected successfully.
     */
    fun onDisconnected(throwable: Throwable?)
}

interface SocketChannelCallback {
    /**
     * Invoked when the client have been joined the channel successfully.
     *
     * @param topic A topic indicating which channel will be joined.
     */
    fun onJoinedChannel(topic: SocketTopic)

    /**
     * Invoked when the client have been left the channel successfully.
     *
     * @param topic A topic indicating which channel will be joined.
     */
    fun onLeftChannel(topic: SocketTopic)

    /**
     * Invoked when something goes wrong while connecting to a channel.
     *
     * @param apiError An [APIError] instance for explaining the failure reason.
     */
    fun onError(apiError: APIError)
}

sealed class SocketCustomEventCallback {
    /**
     * A callback for the [SocketTransactionRequestEvent]
     */
    abstract class TransactionRequestCallback : SocketTransactionRequestEvent, SocketCustomEventCallback()

    /**
     * A callback for the [SocketTransactionConsumptionEvent]
     */
    abstract class TransactionConsumptionCallback : SocketTransactionConsumptionEvent, SocketCustomEventCallback()
}

private interface SocketTransactionRequestEvent {
    fun onTransactionConsumptionRequest(transactionConsumption: TransactionConsumption)
    fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption)
    fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError)
}

private interface SocketTransactionConsumptionEvent {
    fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption)
    fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError)
}
