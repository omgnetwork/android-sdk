package co.omisego.omisego.websocket.listener

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 21/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.User
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.strategy.FilterStrategy
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Test

class FilteredCustomEventListenerTest {
    private val mockUserSocketEvent: SocketEvent<TransactionConsumption> = mock {
        on { socketReceive } doReturn mock<SocketReceive<TransactionConsumption>>()
        on { socketReceive.topic } doReturn "user"
    }
    private val mockTransactionRequestSocketEvent: SocketEvent<TransactionConsumption> = mock {
        on { socketReceive } doReturn mock<SocketReceive<TransactionConsumption>>()
        on { socketReceive.topic } doReturn "transaction_request"
    }
    private val mockTransactionConsumptionSocketEvent: SocketEvent<TransactionConsumption> = mock {
        on { socketReceive } doReturn mock<SocketReceive<TransactionConsumption>>()
        on { socketReceive.topic } doReturn "transaction_consumption"
    }
    private val mockUser: User = mock {
        on { socketTopic } doReturn SocketTopic("user")
    }
    private val mockTransactionRequest: TransactionRequest = mock {
        on { socketTopic } doReturn SocketTopic("transaction_request")
    }
    private val mockTransactionConsumption: TransactionConsumption = mock {
        on { socketTopic } doReturn SocketTopic("transaction_consumption")
    }

    @Test
    fun `TransactionRequestListener should accept an event only if the topic is the same`() {
        val listener = object : TransactionRequestListener(mockTransactionRequest) {
            override fun onTransactionConsumptionRequest(transactionConsumption: TransactionConsumption) {
            }

            override fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption) {
            }

            override fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError) {
            }
        }

        with(listener.strategy) {
            this shouldBeInstanceOf FilterStrategy.Topic::class.java
            accept(mockTransactionRequestSocketEvent) shouldBe true
            accept(mockTransactionConsumptionSocketEvent) shouldBe false
            accept(mockUserSocketEvent) shouldBe false
        }
    }

    @Test
    fun `TransactionConsumptionListener should accept an event only if the topic is the same`() {
        val listener = object : TransactionConsumptionListener(mockTransactionConsumption) {
            override fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption) {
            }

            override fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError) {
            }
        }

        with(listener.strategy) {
            this shouldBeInstanceOf FilterStrategy.Topic::class.java
            listener.strategy.accept(mockTransactionRequestSocketEvent) shouldBe false
            listener.strategy.accept(mockTransactionConsumptionSocketEvent) shouldBe true
            listener.strategy.accept(mockUserSocketEvent) shouldBe false
        }
    }

    @Test
    fun `ListenableTopicListener should accept an event only if the topic is the same`() {
        val listener = object : ListenableTopicListener(mockUser) {
            override fun onSpecificEvent(event: SocketEvent<*>) {
            }
        }

        with(listener.strategy) {
            this shouldBeInstanceOf FilterStrategy.Topic::class.java
            accept(mockTransactionRequestSocketEvent) shouldBe false
            accept(mockTransactionConsumptionSocketEvent) shouldBe false
            accept(mockUserSocketEvent) shouldBe true
        }
    }
}
