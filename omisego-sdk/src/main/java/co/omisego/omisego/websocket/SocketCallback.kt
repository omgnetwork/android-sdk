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

sealed class SocketCustomEventCallback {
    /**
     * A callback for every event in the SocketCustomEvent (see SocketEvent.kt, CustomEventDispatcher.kt ).
     */
    abstract class AnyEventCallback : SocketCustomEventCallback() {
        abstract fun onEventReceived(data: SocketReceive)
    }

    /**
     * A callback for TransactionRequest
     */
    abstract class TransactionRequestCallback : SocketCustomEventCallback() {
        abstract fun onTransactionConsumptionRequest(transactionConsumption: TransactionConsumption)
        abstract fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption)
        abstract fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError)
    }

    /**
     * A callback for the TransactionConsumption
     */
    abstract class TransactionConsumptionCallback : SocketCustomEventCallback() {
        abstract fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption)
        abstract fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError)
    }
}
