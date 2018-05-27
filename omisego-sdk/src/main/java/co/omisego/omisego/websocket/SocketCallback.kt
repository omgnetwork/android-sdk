package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 8/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption

sealed class SocketCustomEventListener {
    /**
     * A listener for every event in the SocketCustomEvent (see SocketEvent.kt, CustomEventDispatcher.kt ).
     */
    abstract class AnyEventListener : SocketCustomEventListener() {
        abstract fun onEventReceived(data: SocketReceive)
    }

    /**
     * A listener for TransactionRequest
     */
    abstract class TransactionRequestListener : SocketCustomEventListener() {
        abstract fun onTransactionConsumptionRequest(transactionConsumption: TransactionConsumption)
        abstract fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption)
        abstract fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError)
    }

    /**
     * A listener for the TransactionConsumption
     */
    abstract class TransactionConsumptionListener : SocketCustomEventListener() {
        abstract fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption)
        abstract fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError)
    }
}
