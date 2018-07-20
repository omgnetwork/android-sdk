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
    var strategy: FilterStrategy
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
    abstract class TransactionRequestListener : SimpleSocketCustomEventListener<SocketEvent<*>>() {
        override var strategy: FilterStrategy = FilterStrategy.Event(allowedEvents)
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

    abstract class TransactionConsumptionListener : SimpleSocketCustomEventListener<SocketEvent<*>>() {
        override var strategy: FilterStrategy = FilterStrategy.Event(allowedEvents)
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
