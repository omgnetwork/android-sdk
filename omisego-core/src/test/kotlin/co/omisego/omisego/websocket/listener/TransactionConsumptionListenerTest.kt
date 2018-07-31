package co.omisego.omisego.websocket.listener

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 25/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.websocket.event.TransactionConsumptionFinalizedEvent
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class TransactionConsumptionListenerTest {
    private val mockTransactionConsumptionFinalizedSuccessEvent: TransactionConsumptionFinalizedEvent = mock {
        on { socketReceive } doReturn mock<SocketReceive<TransactionConsumption>>()
        on { socketReceive.data } doReturn mock<TransactionConsumption>()
    }

    private val mockTransactionConsumptionFinalizedFailEvent: TransactionConsumptionFinalizedEvent = mock {
        on { socketReceive } doReturn mock<SocketReceive<TransactionConsumption>>()
        on { socketReceive.data } doReturn mock<TransactionConsumption>()
        on { socketReceive.error } doReturn APIError(ErrorCode.TRANSACTION_UNAUTHORIZED_AMOUNT_OVERRIDE, "error")
    }

    private val listener = spy(object : TransactionConsumptionListener() {
        override fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption) {
            // Do something
        }

        override fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError) {
            // Do something
        }
    })

    @Test
    fun `dispatch TransactionConsumptionFinalizedEvent correctly`() {
        listener.onSpecificEvent(mockTransactionConsumptionFinalizedSuccessEvent)
        listener.onSpecificEvent(mockTransactionConsumptionFinalizedFailEvent)
        verify(listener).onTransactionConsumptionFinalizedSuccess(any())
        verify(listener).onTransactionConsumptionFinalizedFail(any(), any())
    }
}
