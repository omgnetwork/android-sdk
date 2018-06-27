package co.omisego.omisego.websocket

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption

sealed class SocketEvent<T : SocketReceive.SocketData> {
    abstract val socketReceive: SocketReceive<T>
}

sealed class SocketErrorEvent<T : SocketReceive.SocketData> : SocketEvent<T>() {
    abstract val apiError: APIError
}

// Transaction consumption events
data class TransactionConsumptionRequestEvent(
    override val socketReceive: SocketReceive<TransactionConsumption>
) : SocketEvent<TransactionConsumption>()

data class TransactionConsumptionFinalizedSuccessEvent(
    override val socketReceive: SocketReceive<TransactionConsumption>
) : SocketEvent<TransactionConsumption>()

data class TransactionConsumptionFinalizedFailEvent(
    override val socketReceive: SocketReceive<TransactionConsumption>,
    override val apiError: APIError
) : SocketErrorEvent<TransactionConsumption>()