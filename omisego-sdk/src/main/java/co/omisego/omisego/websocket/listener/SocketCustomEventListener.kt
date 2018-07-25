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
    fun onEvent(event: SocketEvent<*>)

    companion object {
        /**
         * A convenient method for listening for the specific event.
         *
         * @param lambda A lambda which receives the `SocketEvent` object without regarding the `SocketTopic`.
         */
        inline fun <reified Event : SocketEvent<*>> forEvent(
            crossinline lambda: (Event) -> Unit
        ): SocketCustomEventListener {
            return object : SimpleSocketCustomEventListener() {
                override val strategy: FilterStrategy = FilterStrategy.Event(listOf(Event::class.java))
                override fun onSpecificEvent(event: SocketEvent<*>) {
                    lambda(event as Event)
                }
            }
        }

        /**
         * A convenient method for listening for any events, but the events will be filtered out by the provided `FilterStrategy`
         *
         * @param listener A [SocketCustomEventListener] implementation
         * @param strategy A [FilterStrategy] that used for filtering an event.
         */
        fun forEvents(
            strategy: FilterStrategy,
            listener: SocketCustomEventListener
        ): SocketCustomEventListener {
            return object : SimpleSocketCustomEventListener() {
                override val strategy: FilterStrategy = strategy
                override fun onSpecificEvent(event: SocketEvent<*>) {
                    listener.onEvent(event)
                }
            }
        }

        /**
         * A convenient method for listening for any events, but the events will be filtered out by the provided `FilterStrategy`.
         *
         * @param strategy A [FilterStrategy] that used for filtering an event.
         * @param lambda A lambda which receives the `SocketEvent` object.
         */
        inline fun forEvents(
            strategy: FilterStrategy,
            crossinline lambda: (SocketEvent<out SocketReceive.SocketData>) -> Unit
        ): SocketCustomEventListener {
            return object : SimpleSocketCustomEventListener() {
                override val strategy: FilterStrategy = strategy
                override fun onSpecificEvent(event: SocketEvent<*>) {
                    lambda(event)
                }
            }
        }
    }

    @Deprecated(
        message = "Remove SocketCustomEventListener.TransactionConsumptionListener and use TransactionConsumptionListener instead.",
        level = DeprecationLevel.ERROR
    )
    abstract class TransactionConsumptionListener : SimpleSocketCustomEventListener()

    @Deprecated(
        message = "Remove SocketCustomEventListener.TransactionRequestListener and use TransactionRequestListener instead.",
        level = DeprecationLevel.ERROR
    )
    abstract class TransactionRequestListener : SimpleSocketCustomEventListener()
}
