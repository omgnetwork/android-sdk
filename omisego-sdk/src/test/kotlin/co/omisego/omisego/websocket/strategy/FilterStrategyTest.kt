package co.omisego.omisego.websocket.strategy

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 19/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionFinalizedEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionRequestEvent
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class FilterStrategyTest {
    val topic = SocketTopic("topic")
    private val noneStrategy: FilterStrategy by lazy { FilterStrategy.None() }
    private val topicStrategy: FilterStrategy by lazy { FilterStrategy.Topic(topic) }
    private val eventStrategy: FilterStrategy by lazy {
        FilterStrategy.Event(
            listOf(
                TransactionConsumptionRequestEvent::class.java
            )
        )
    }

    private val mockMatchedTopicSocketEvent = mock<SocketEvent<TransactionConsumption>> {
        on { socketReceive } doReturn mock<SocketReceive<TransactionConsumption>>()
        on { socketReceive.topic } doReturn "topic"
    }
    private val mockUnMatchedTopicSocketEvent = mock<SocketEvent<TransactionConsumption>> {
        on { socketReceive } doReturn mock<SocketReceive<TransactionConsumption>>()
        on { socketReceive.topic } doReturn "topic2"
    }
    private val mockExistEventSocketEvent = mock<TransactionConsumptionRequestEvent>()
    private val mockNonExistEventSocketEvent = mock<TransactionConsumptionFinalizedEvent>()

    @Test
    fun `topic_strategy should accept only if the topic matched with its own`() {
        topicStrategy.accept(mockMatchedTopicSocketEvent) shouldEqualTo true
        topicStrategy.accept(mockUnMatchedTopicSocketEvent) shouldEqualTo false
    }

    @Test
    fun `event_strategy should accept only if the event exist in the list`() {
        eventStrategy.accept(mockExistEventSocketEvent) shouldEqualTo true
        eventStrategy.accept(mockNonExistEventSocketEvent) shouldEqualTo false
    }

    @Test
    fun `none_strategy should accept all events`() {
        noneStrategy.accept(mockExistEventSocketEvent) shouldEqualTo true
        noneStrategy.accept(mockNonExistEventSocketEvent) shouldEqualTo true
        noneStrategy.accept(mockMatchedTopicSocketEvent) shouldEqualTo true
        noneStrategy.accept(mockUnMatchedTopicSocketEvent) shouldEqualTo true
    }
}
