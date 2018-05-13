package co.omisego.omisego.model.socket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.utils.Either
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.enum.SocketSystemEvent

data class SocketReceive(
    val topic: String,
    val event: Either<SocketSystemEvent, SocketCustomEvent>,
    val ref: String?,
    val data: SocketReceiveData?,
    val version: String,
    val success: Boolean,
    val error: APIError?
)

sealed class SocketReceiveData {
    data class SocketConsumeTransaction(val data: TransactionConsumption) : SocketReceiveData()
    data class Other(val data: Map<String, Any>) : SocketReceiveData()
}
