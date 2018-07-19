package co.omisego.omisego.websocket.event

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption

sealed class SocketEvent<T : SocketReceive.SocketData> {
    abstract val socketReceive: SocketReceive<T>
}

// Transaction consumption events
data class TransactionConsumptionRequestEvent(
    override val socketReceive: SocketReceive<TransactionConsumption>
) : SocketEvent<TransactionConsumption>()

data class TransactionConsumptionFinalizedEvent(
    override val socketReceive: SocketReceive<TransactionConsumption>
) : SocketEvent<TransactionConsumption>()
