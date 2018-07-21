package co.omisego.omisego.websocket.listener

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 21/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.operation.Listenable
import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.strategy.FilterStrategy

abstract class TransactionRequestTopicListener(
    transactionRequest: TransactionRequest
) : SocketCustomEventListener.TransactionRequestListener() {
    final override var strategy: FilterStrategy = FilterStrategy.Topic(transactionRequest.socketTopic)
}

abstract class TransactionConsumptionTopicListener(
    transactionConsumption: TransactionConsumption
) : SocketCustomEventListener.TransactionConsumptionListener() {
    final override var strategy: FilterStrategy = FilterStrategy.Topic(transactionConsumption.socketTopic)
}

abstract class ListenableTopicListener(
    listenable: Listenable
) : SimpleSocketCustomEventListener<SocketEvent<*>>() {
    final override var strategy: FilterStrategy = FilterStrategy.Topic(listenable.socketTopic)
}