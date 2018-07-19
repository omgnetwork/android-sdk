package co.omisego.omisego.websocket.listener

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.websocket.event.TransactionConsumptionFinalizedEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionListener
import co.omisego.omisego.websocket.strategy.FilterStrategy
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.mock
import org.junit.Before
import org.junit.Test

class SimpleSocketCustomEventListenerTest {
    private val listener: TransactionConsumptionListener by lazy {
        spy(
            object : TransactionConsumptionListener() {
                override var strategy: FilterStrategy = mock()
                override fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption) {
                }

                override fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError) {
                }
            }
        )
    }

    private val mockTransactionConsumption: TransactionConsumption = mock()
    private val mockAPIError: APIError = mock()
    private val mockResponseSuccess: SocketReceive<TransactionConsumption> = mock()
    private val mockResponseFail: SocketReceive<TransactionConsumption> = mock()
    private val mockResponseUnknownError: SocketReceive<TransactionConsumption> = mock()

    @Before
    fun mockResponse() {
        whenever(mockResponseFail.error).thenReturn(mockAPIError)
        whenever(mockResponseFail.data).thenReturn(mockTransactionConsumption)
        whenever(mockResponseSuccess.error).thenReturn(null)
        whenever(mockResponseSuccess.data).thenReturn(mockTransactionConsumption)
        whenever(mockResponseUnknownError.error).thenReturn(null)
        whenever(mockResponseUnknownError.data).thenReturn(null)
    }

    @Test
    fun `dispatch function should be invoked onSuccess correctly`() {
        listener.onSpecificEvent(TransactionConsumptionFinalizedEvent(mockResponseSuccess))

        verify(listener, times(1)).onTransactionConsumptionFinalizedSuccess(mockTransactionConsumption)
        verify(listener, times(0)).onTransactionConsumptionFinalizedFail(mockTransactionConsumption, mockAPIError)
    }

    @Test
    fun `dispatch function should be invoked onFailed correctly`() {
        listener.onSpecificEvent(TransactionConsumptionFinalizedEvent(mockResponseFail))

        verify(listener, times(1)).onTransactionConsumptionFinalizedFail(mockTransactionConsumption, mockAPIError)
        verify(listener, times(0)).onTransactionConsumptionFinalizedSuccess(mockTransactionConsumption)
    }

    @Test
    fun `onEvent function should called onSpecificEvent only if the strategy is accept the event`() {
        val event = TransactionConsumptionFinalizedEvent(mockResponseSuccess)

        /* Only if the strategy accept this event, the onSpecificEvent should be immediately invoked. */
        whenever(listener.strategy.accept(event)).thenReturn(true)
        listener.onEvent(event)

        verify(listener, times(1)).onSpecificEvent(event)
    }

    @Test
    fun `onEvent function should not called onSpecificEvent only if the strategy is not accept the event`() {
        val event = TransactionConsumptionFinalizedEvent(mockResponseSuccess)

        whenever(listener.strategy.accept(event)).thenReturn(false)
        listener.onEvent(event)

        verify(listener, times(0)).onSpecificEvent(event)
    }
}
