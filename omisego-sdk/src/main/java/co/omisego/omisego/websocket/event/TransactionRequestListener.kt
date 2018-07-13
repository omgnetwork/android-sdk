package co.omisego.omisego.websocket.event

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.websocket.listener.SimpleSocketCustomEventListener

abstract class TransactionRequestListener : SimpleSocketCustomEventListener<SocketEvent<*>>(allowedEvents) {

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

    companion object {
        private val allowedEvents = listOf(
            TransactionConsumptionRequestEvent::class.java,
            TransactionConsumptionFinalizedEvent::class.java
        )
    }
}
