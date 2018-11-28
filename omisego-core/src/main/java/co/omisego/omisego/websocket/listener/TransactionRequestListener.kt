package co.omisego.omisego.websocket.listener

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 23/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.TransactionConsumption
import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionFinalizedEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionRequestEvent
import co.omisego.omisego.websocket.strategy.FilterStrategy

abstract class TransactionRequestListener : SimpleSocketCustomEventListener() {
    final override val strategy: FilterStrategy = FilterStrategy.None()

    final override fun onSpecificEvent(event: SocketEvent<*>) {
        when (event) {
            is TransactionConsumptionRequestEvent -> event.socketReceive.data?.let(::onTransactionConsumptionRequest)
            is TransactionConsumptionFinalizedEvent -> event.socketReceive.dispatch(
                onSuccess = ::onTransactionConsumptionFinalizedSuccess,
                onError = ::onTransactionConsumptionFinalizedFail
            )
        }
    }

    abstract fun onTransactionConsumptionRequest(transactionConsumption: TransactionConsumption)
    abstract fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption)
    abstract fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError)
}
