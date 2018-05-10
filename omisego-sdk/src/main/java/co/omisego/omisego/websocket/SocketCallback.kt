package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 8/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption

interface SocketConnectionCallback {
    fun onConnected()
    fun onDisconnected()
}

interface SocketTopicCallback {
    fun onSubscribedTopic()
    fun onUnsubscribedTopic()
    fun onError(apiError: APIError)
}

sealed class SocketTransactionEvent {
    abstract class RequestEvent : SocketTransactionRequestEvent, SocketTransactionEvent()
    abstract class ConsumptionEvent : SocketTransactionConsumptionEvent, SocketTransactionEvent()
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
