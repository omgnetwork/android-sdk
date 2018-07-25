package co.omisego.omisego.websocket.listener

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 25/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.websocket.event.TransactionConsumptionFinalizedEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionRequestEvent
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class ForEventTest {
    @Test
    fun `forEvent should not invoke the lambda if the unrelated event has come`() {
        val mockSpecificEventLambda: (TransactionConsumptionRequestEvent) -> Unit = mock()
        SocketCustomEventListener.forEvent(mockSpecificEventLambda).onEvent(mock<TransactionConsumptionFinalizedEvent>())
        verify(mockSpecificEventLambda, times(0)).invoke(any())
    }

    @Test
    fun `forEvent should invoke the lambda if the related event has come`() {
        val mockSpecificEventLambda: (TransactionConsumptionRequestEvent) -> Unit = mock()
        SocketCustomEventListener.forEvent(mockSpecificEventLambda).onEvent(mock<TransactionConsumptionRequestEvent>())
        verify(mockSpecificEventLambda, times(1)).invoke(any())
    }
}
