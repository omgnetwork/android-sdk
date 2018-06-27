package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.websocket.SocketChannelListener
import co.omisego.omisego.websocket.SocketCustomEventListener
import co.omisego.omisego.websocket.SocketErrorEvent
import co.omisego.omisego.websocket.SocketEvent
import co.omisego.omisego.websocket.TransactionConsumptionFinalizedFailEvent
import co.omisego.omisego.websocket.TransactionConsumptionFinalizedSuccessEvent
import co.omisego.omisego.websocket.TransactionConsumptionRequestEvent
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.enum.SocketCustomEvent.OTHER
import co.omisego.omisego.websocket.enum.SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED
import co.omisego.omisego.websocket.enum.SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST

/**
 * A listener for dispatch the [SocketCustomEventListener] events.
 */
class CustomEventDispatcher(
    override val socketChannelListener: SocketChannelListener
) : SocketDispatcherContract.CustomEventDispatcher {

    override val customEventListeners: MutableSet<SocketCustomEventListener> = linkedSetOf()

    override fun handleEvent(customEvent: SocketCustomEvent, response: SocketReceive<*>) {
        val event = response.mapToEvent(customEvent) ?: return
        customEventListeners.forEach { it.onEvent(event) }
    }

    private fun SocketReceive<*>.mapToEvent(customEvent: SocketCustomEvent): SocketEvent<*>? {
        return when (customEvent) {
            TRANSACTION_CONSUMPTION_REQUEST -> buildEvent<TransactionConsumption>(
                eventProvider = { data -> TransactionConsumptionRequestEvent(data) },
                errorEventProvider = { _, _ -> null },
                errorFallback = { error -> socketChannelListener.onError(error) }
            )
            TRANSACTION_CONSUMPTION_FINALIZED -> buildEvent<TransactionConsumption>(
                eventProvider = { data -> TransactionConsumptionFinalizedSuccessEvent(data) },
                errorEventProvider = { data, error -> TransactionConsumptionFinalizedFailEvent(data, error) },
                errorFallback = { error -> socketChannelListener.onError(error) }
            )
            OTHER -> {
                error?.let { socketChannelListener.onError(it) }
                null
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T : SocketReceive.SocketData> SocketReceive<*>.buildEvent(
        eventProvider: (SocketReceive<T>) -> SocketEvent<T>?,
        errorEventProvider: (SocketReceive<T>, APIError) -> SocketErrorEvent<T>?,
        errorFallback: (APIError) -> Unit = {}
    ): SocketEvent<T>? {
        val event = when {
            data is T && error != null -> errorEventProvider(this as SocketReceive<T>, error)
            data is T -> eventProvider(this as SocketReceive<T>)
            else -> null
        }

        if (event == null) {
            error?.let(errorFallback)
        }

        return event
    }
}
