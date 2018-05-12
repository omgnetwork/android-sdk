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

interface SocketConnectionCallback {
    fun onConnected()
    fun onDisconnected(throwable: Throwable?)
}

interface SocketTopicCallback {
    fun onSubscribedTopic(topic: SocketTopic)
    fun onUnSubscribedTopic(topic: SocketTopic)
    fun onError(apiError: APIError)
}

sealed class SocketListenEvent {
    abstract class TransactionRequestEvent : SocketTransactionRequestEvent, SocketListenEvent()
    abstract class TransactionConsumptionEvent : SocketTransactionConsumptionEvent, SocketListenEvent()
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
