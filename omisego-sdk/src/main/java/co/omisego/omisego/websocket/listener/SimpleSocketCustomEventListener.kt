package co.omisego.omisego.websocket.listener

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 18/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.strategy.FilterStrategy

/**
 * A [SocketCustomEventListener] that can be filtered for a specific type of event.
 */

abstract class SimpleSocketCustomEventListener<Event : SocketEvent<*>> : SocketCustomEventListener {
    abstract val strategy: FilterStrategy

    @Suppress("UNCHECKED_CAST")
    final override fun onEvent(event: SocketEvent<*>) {
        if (strategy.accept(event)) {
            onSpecificEvent(event as Event)
        }
    }

    abstract fun onSpecificEvent(event: Event)

    companion object {
        internal inline fun <reified T : SocketReceive.SocketData> SocketReceive<T>.dispatch(
            onSuccess: (T) -> Any,
            onError: (T, APIError) -> Any
        ) {
            when {
                data is T && error != null -> onError(data, error)
                data is T -> onSuccess(data)
                else -> Unit
            }
        }
    }
}
