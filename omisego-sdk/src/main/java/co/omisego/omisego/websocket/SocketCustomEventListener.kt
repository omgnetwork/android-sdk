package co.omisego.omisego.websocket

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.websocket.event.SocketEvent

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 8/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

interface SocketCustomEventListener {

    fun onEvent(event: SocketEvent<*>)

    companion object {
        inline fun <reified Event : SocketEvent<*>> forEvent(
            crossinline lambda: (Event) -> Unit
        ): SocketCustomEventListener {
            return object : SimpleSocketCustomEventListener<Event>(Event::class.java) {
                override fun onSpecificEvent(event: Event) {
                    lambda(event)
                }
            }
        }

        /**
         * Wrap a [SocketCustomEventListener] by making sure it's called only for events matching the given list
         */
        fun forEvents(
            listener: SocketCustomEventListener,
            events: List<Class<out SocketEvent<*>>>
        ): SocketCustomEventListener {
            return object : SimpleSocketCustomEventListener<SocketEvent<*>>(events) {
                override fun onSpecificEvent(event: SocketEvent<*>) {
                    listener.onEvent(event)
                }
            }
        }

        /**
         * Wrap a [SocketCustomEventListener] by making sure it's called only for events matching the given list
         */
        inline fun forEvents(
            crossinline lambda: (SocketEvent<out SocketReceive.SocketData>) -> Unit,
            events: List<Class<out SocketEvent<*>>>
        ): SocketCustomEventListener {
            return object : SimpleSocketCustomEventListener<SocketEvent<*>>(events) {
                override fun onSpecificEvent(event: SocketEvent<*>) {
                    lambda(event)
                }
            }
        }
    }
}

/**
 * A [SocketCustomEventListener] than can be filtered for a specific type of event.
 */
// We don't specify the type argument for SocketEvent to have a nicer syntax, because of this, you won't be able to use
// supertypes with this (i.e have a SocketErrorEvent listener).
abstract class SimpleSocketCustomEventListener<Event : SocketEvent<*>>(
    private val allowedEvents: List<Class<out Event>>
) : SocketCustomEventListener {

    constructor(allowedEvent: Class<Event>) : this(listOf(allowedEvent))

    @Suppress("UNCHECKED_CAST")
    final override fun onEvent(event: SocketEvent<*>) {
        if (allowedEvents.any { it.isAssignableFrom(event::class.java) }) {
            onSpecificEvent(event as Event)
        }
    }

    abstract fun onSpecificEvent(event: Event)
}