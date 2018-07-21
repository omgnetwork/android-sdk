package co.omisego.omisego.websocket.strategy

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 19/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.event.SocketEvent

/**
 * A strategy that is used in the [SimpleSocketCustomEventListener] for filtering the events.
 * There're currently 4 types of strategy including:
 *
 * Event: Filtering events by accept only if the event type is contained in the given [SocketEvent] classes (e.g. TransactionConsumptionRequestEvent, TransactionConsumptionFinalizedEvent).
 * Topic: Filtering events by accept only if the event's `SocketTopic` matches the given [SocketTopic].
 * Custom: Custom filtering by implementing a lambda that receives [SocketEvent] to return a boolean.
 * None: Accept all events.
 */
sealed class FilterStrategy {
    abstract fun accept(event: SocketEvent<*>): Boolean

    class Event(private val allowedEvents: List<Class<out SocketEvent<*>>>) : FilterStrategy() {
        override fun accept(event: SocketEvent<*>) = allowedEvents.any { it.isAssignableFrom(event::class.java) }
    }

    class Topic(private val topic: SocketTopic) : FilterStrategy() {
        override fun accept(event: SocketEvent<*>) = topic.name == event.socketReceive.topic
    }

    class Custom(private val customFiltering: (event: SocketEvent<*>) -> Boolean) : FilterStrategy() {
        override fun accept(event: SocketEvent<*>) = customFiltering(event)
    }

    class None : FilterStrategy() {
        override fun accept(event: SocketEvent<*>) = true
    }
}
