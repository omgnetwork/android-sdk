package co.omisego.omisego.websocket.event

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.websocket.SimpleSocketCustomEventListener

abstract class TransactionConsumptionListener : SimpleSocketCustomEventListener<SocketEvent<*>>(allowedEvents) {

    final override fun onSpecificEvent(event: SocketEvent<*>) {
        when (event) {
            is TransactionConsumptionFinalizedEvent -> event.socketReceive.dispatch(
                onSuccess = ::onTransactionConsumptionFinalizedSuccess,
                onError = ::onTransactionConsumptionFinalizedFail
            )
        }
    }

    abstract fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption)
    abstract fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError)

    companion object {
        private val allowedEvents = listOf(
            TransactionConsumptionFinalizedEvent::class.java
        )
    }
}