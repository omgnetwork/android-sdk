package co.omisego.omisego.websocket.strategy

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 19/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.TransactionConsumption
import co.omisego.omisego.utils.Either
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionFinalizedEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionRequestEvent
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class FilterStrategyTest {
    val topic = SocketTopic("topic1")
    private val noneStrategy: FilterStrategy by lazy { FilterStrategy.None() }
    private val topicStrategy: FilterStrategy by lazy { FilterStrategy.Topic(topic) }
    private val customStrategy: FilterStrategy by lazy {
        FilterStrategy.Custom {
            it.socketReceive.topic.contains("2") || it.socketReceive.event == Either.Right(SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST)
        }
    }
    private val eventStrategy: FilterStrategy by lazy {
        FilterStrategy.Event(
            listOf(
                TransactionConsumptionRequestEvent::class.java
            )
        )
    }

    private val mockTopicOneSocketEvent = mock<SocketEvent<TransactionConsumption>> {
        on { socketReceive } doReturn mock<SocketReceive<TransactionConsumption>>()
        on { socketReceive.topic } doReturn "topic1"
    }
    private val mockTopicTwoSocketEvent = mock<SocketEvent<TransactionConsumption>> {
        on { socketReceive } doReturn mock<SocketReceive<TransactionConsumption>>()
        on { socketReceive.topic } doReturn "topic2"
    }
    private val mockConsumptionRequestEvent = mock<TransactionConsumptionRequestEvent> {
        on { socketReceive } doReturn mock<SocketReceive<TransactionConsumption>>()
        on { socketReceive.topic } doReturn "topic3"
        on { socketReceive.event } doReturn Either.Right(SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST)
    }
    private val mockConsumptionFinalizedEvent = mock<TransactionConsumptionFinalizedEvent> {
        on { socketReceive } doReturn mock<SocketReceive<TransactionConsumption>>()
        on { socketReceive.topic } doReturn "topic4"
        on { socketReceive.event } doReturn Either.Right(SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED)
    }

    @Test
    fun `topic_strategy should accept only if the topic matched with its own`() {
        topicStrategy.accept(mockTopicOneSocketEvent) shouldEqualTo true
        topicStrategy.accept(mockTopicTwoSocketEvent) shouldEqualTo false
    }

    @Test
    fun `event_strategy should accept only if the event exist in the list`() {
        eventStrategy.accept(mockConsumptionRequestEvent) shouldEqualTo true
        eventStrategy.accept(mockConsumptionFinalizedEvent) shouldEqualTo false
    }

    @Test
    fun `custom_strategy should be accept only if the custom filtering is return true`() {
        customStrategy.accept(mockConsumptionRequestEvent) shouldEqualTo true
        customStrategy.accept(mockTopicTwoSocketEvent) shouldEqualTo true
        customStrategy.accept(mockConsumptionFinalizedEvent) shouldEqualTo false
        customStrategy.accept(mockTopicOneSocketEvent) shouldEqualTo false
    }

    @Test
    fun `none_strategy should accept all events`() {
        noneStrategy.accept(mockConsumptionRequestEvent) shouldEqualTo true
        noneStrategy.accept(mockConsumptionFinalizedEvent) shouldEqualTo true
        noneStrategy.accept(mockTopicOneSocketEvent) shouldEqualTo true
        noneStrategy.accept(mockTopicTwoSocketEvent) shouldEqualTo true
    }
}
