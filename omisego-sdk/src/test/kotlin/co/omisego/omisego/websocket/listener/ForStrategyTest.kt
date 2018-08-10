package co.omisego.omisego.websocket.listener

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 25/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.strategy.FilterStrategy
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class ForStrategyTest {
    @Test
    fun `forStrategy should invoke the lambda if the event match with the filter strategy`() {
        val lambda: (SocketEvent<*>) -> Unit = mock()
        val forStrategyListener = SocketCustomEventListener.forStrategy(FilterStrategy.Custom { it.socketReceive.success }, lambda)
        forStrategyListener.onEvent(mock {
            on { socketReceive } doReturn mock<SocketReceive<TransactionConsumption>>()
            on { socketReceive.success } doReturn true
        })

        verify(lambda, times(1)).invoke(any())
    }

    @Test
    fun `forStrategy should not invoke the lambda if the event does not match with the filter strategy`() {
        val lambda: (SocketEvent<*>) -> Unit = mock()
        val forStrategyListener = SocketCustomEventListener.forStrategy(FilterStrategy.Custom { it.socketReceive.success }, lambda)
        forStrategyListener.onEvent(mock {
            on { socketReceive } doReturn mock<SocketReceive<TransactionConsumption>>()
            on { socketReceive.success } doReturn false
        })

        verify(lambda, never()).invoke(any())
    }
}
