package co.omisego.omisego.websocket.listener

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 8/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.strategy.FilterStrategy

interface SocketCustomEventListener {
    abstract var strategy: FilterStrategy
    fun onEvent(event: SocketEvent<*>)

    companion object {
        inline fun <reified Event : SocketEvent<*>> forEvent(
            crossinline lambda: (Event) -> Unit
        ): SocketCustomEventListener {
            return object : SimpleSocketCustomEventListener<Event>() {
                override var strategy: FilterStrategy = FilterStrategy.Event(listOf(Event::class.java))
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
            strategy: FilterStrategy
        ): SocketCustomEventListener {
            return object : SimpleSocketCustomEventListener<SocketEvent<*>>() {
                override var strategy: FilterStrategy = strategy
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
            strategy: FilterStrategy
        ): SocketCustomEventListener {
            return object : SimpleSocketCustomEventListener<SocketEvent<*>>() {
                override var strategy: FilterStrategy = strategy
                override fun onSpecificEvent(event: SocketEvent<*>) {
                    lambda(event)
                }
            }
        }
    }
}
