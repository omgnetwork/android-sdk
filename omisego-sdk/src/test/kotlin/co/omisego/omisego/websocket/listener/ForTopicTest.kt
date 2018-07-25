package co.omisego.omisego.websocket.listener

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 25/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.operation.Listenable
import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionRequestEvent
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class ForTopicTest {
    data class TestListenable(override val socketTopic: SocketTopic) : Listenable

    private val mockTopicEventLambda: (SocketEvent<*>) -> Unit = mock()
    private val forTopicListener: SocketCustomEventListener = SocketCustomEventListener.forTopic(
        TestListenable(SocketTopic("topic1")),
        mockTopicEventLambda
    )

    @Test
    fun `forTopic should invoke the lambda if the related topic has come`() {
        val socketReceive = mock<SocketReceive<TransactionConsumption>> {
            on { topic } doReturn "topic1"
        }
        val event = TransactionConsumptionRequestEvent(socketReceive)

        forTopicListener.onEvent(event)

        verify(mockTopicEventLambda, times(1)).invoke(event)
    }

    @Test
    fun `forTopic should not invoke the lambda if the unrelated topic has come`() {
        val socketReceive = mock<SocketReceive<TransactionConsumption>> {
            on { topic } doReturn "topic2"
        }
        val event = TransactionConsumptionRequestEvent(socketReceive)

        forTopicListener.onEvent(event)

        verify(mockTopicEventLambda, times(0)).invoke(event)
    }
}
