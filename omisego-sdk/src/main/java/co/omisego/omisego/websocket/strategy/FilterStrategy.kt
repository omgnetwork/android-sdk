package co.omisego.omisego.websocket.strategy

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 19/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.event.SocketEvent

sealed class FilterStrategy {
    abstract fun accept(event: SocketEvent<*>): Boolean

    class Event(private val allowedEvents: List<Class<out SocketEvent<*>>>) : FilterStrategy() {
        override fun accept(event: SocketEvent<*>) = allowedEvents.any { it.isAssignableFrom(event::class.java) }
    }

    class Topic(private val topic: SocketTopic) : FilterStrategy() {
        override fun accept(event: SocketEvent<*>) = topic.name == event.socketReceive.topic
    }

    class None : FilterStrategy() {
        override fun accept(event: SocketEvent<*>) = true
    }
}
