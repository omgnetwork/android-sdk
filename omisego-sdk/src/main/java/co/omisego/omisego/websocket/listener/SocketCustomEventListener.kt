package co.omisego.omisego.websocket.listener

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 8/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionFinalizedEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionRequestEvent
import co.omisego.omisego.websocket.strategy.FilterStrategy

interface SocketCustomEventListener {
    val strategy: FilterStrategy
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
            return object : SimpleSocketCustomEventListener<Event>() {
                override val strategy: FilterStrategy = FilterStrategy.Event(listOf(Event::class.java))
                override fun onSpecificEvent(event: Event) {
                    lambda(event)
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
            listener: SocketCustomEventListener,
            strategy: FilterStrategy
        ): SocketCustomEventListener {
            return object : SimpleSocketCustomEventListener<SocketEvent<*>>() {
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
            return object : SimpleSocketCustomEventListener<SocketEvent<*>>() {
                override val strategy: FilterStrategy = strategy
                override fun onSpecificEvent(event: SocketEvent<*>) {
                    lambda(event)
                }
            }
        }
    }

    abstract class TransactionRequestListener(
        final override val strategy: FilterStrategy = FilterStrategy.Event(allowedEvents)
    ) : SimpleSocketCustomEventListener<SocketEvent<*>>() {
        final override fun onSpecificEvent(event: SocketEvent<*>) {
            when (event) {
                is TransactionConsumptionRequestEvent -> event.socketReceive.data?.let(::onTransactionConsumptionRequest)
                is TransactionConsumptionFinalizedEvent -> event.socketReceive.dispatch(
                    onSuccess = ::onTransactionConsumptionFinalizedSuccess,
                    onError = ::onTransactionConsumptionFinalizedFail
                )
            }
        }

        abstract fun onTransactionConsumptionRequest(transactionConsumption: TransactionConsumption)
        abstract fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption)
        abstract fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError)

        companion object {
            internal val allowedEvents = listOf(
                TransactionConsumptionRequestEvent::class.java,
                TransactionConsumptionFinalizedEvent::class.java
            )
        }
    }

    abstract class TransactionConsumptionListener(
        final override val strategy: FilterStrategy = FilterStrategy.Event(allowedEvents)
    ) : SimpleSocketCustomEventListener<SocketEvent<*>>() {
        final override fun onSpecificEvent(event: SocketEvent<*>) {
            when (event) {
                is TransactionConsumptionFinalizedEvent -> event.socketReceive.dispatch(
                    onSuccess = ::onTransactionConsumptionFinalizedSuccess,
                    onError = ::onTransactionConsumptionFinalizedFail
                )
            }
        }

        abstract fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption)
        abstract fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError)

        companion object {
            private val allowedEvents = listOf(
                TransactionConsumptionFinalizedEvent::class.java
            )
        }
    }
}
