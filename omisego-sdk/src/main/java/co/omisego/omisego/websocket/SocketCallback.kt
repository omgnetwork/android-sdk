package co.omisego.omisego.websocket

import co.omisego.omisego.model.transaction.consumption.TransactionConsumption

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 8/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

interface SocketConnectionCallback {
    fun onConnected()
    fun onDisconnected()
}

interface SocketTopicCallback {
    fun onSubscribedTopic()
    fun onUnsubscribedTopic()
    fun onError()
}

interface SocketTransactionRequestEvent {
    fun onTransactionConsumptionRequest(transactionConsumption: TransactionConsumption)
    fun onTransactionConsumptionSuccess(transactionConsumption: TransactionConsumption)
    fun onTransactionConsumptionFailed()
}
