package co.omisego.omisego.websocket.event

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 21/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.TransactionConsumption

sealed class SocketEvent<T : SocketReceive.SocketData> {
    abstract val socketReceive: SocketReceive<T>
}

data class TransactionConsumptionRequestEvent(
    override val socketReceive: SocketReceive<TransactionConsumption>
) : SocketEvent<TransactionConsumption>()

data class TransactionConsumptionFinalizedEvent(
    override val socketReceive: SocketReceive<TransactionConsumption>
) : SocketEvent<TransactionConsumption>()
