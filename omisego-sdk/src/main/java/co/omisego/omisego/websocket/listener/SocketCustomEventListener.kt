package co.omisego.omisego.websocket.listener

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 8/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.operation.Listenable
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
        ): SimpleSocketCustomEventListener {
            return object : SimpleSocketCustomEventListener() {
                override val strategy: FilterStrategy = FilterStrategy.Event(listOf(Event::class.java))
                override fun onSpecificEvent(event: SocketEvent<*>) {
                    lambda(event as Event)
                }
            }
        }

        /**
         * A convenient method for listening for the specific topic.
         *
         * @param listenable A listenable object
         * @param lambda A lambda which receives the `SocketEvent` object regarding the topic.
         */
        inline fun forListenable(
            listenable: Listenable,
            crossinline lambda: (SocketEvent<*>) -> Unit
        ): SocketCustomEventListener {
            return object : SimpleSocketCustomEventListener() {
                override val strategy: FilterStrategy = FilterStrategy.Topic(listenable.socketTopic)
                override fun onSpecificEvent(event: SocketEvent<*>) {
                    lambda(event)
                }
            }
        }

        /**
         * A convenient method for listening to the event by using the [FilterStrategy] to filter the incoming event.
         *
         * @param strategy A [FilterStrategy] to use for filtering the incoming event.
         * @param lambda A lambda which receives the `SocketEvent` object regarding the topic.
         */
        inline fun forStrategy(
            strategy: FilterStrategy,
            crossinline lambda: (SocketEvent<*>) -> Unit
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
