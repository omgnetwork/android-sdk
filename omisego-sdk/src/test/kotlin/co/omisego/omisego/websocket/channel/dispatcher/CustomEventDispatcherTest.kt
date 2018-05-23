package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.utils.Either
import co.omisego.omisego.websocket.SocketCustomEventCallback
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import org.amshove.kluent.mock
import org.junit.Before
import org.junit.Test

class CustomEventDispatcherTest {

    private val mockData: TransactionConsumption = mock()
    private val dataTxRequest: SocketReceive = SocketReceive(
        "topic",
        event = Either.Right(SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST),
        data = mockData,
        version = "1",
        success = true
    )

    private val dataTxFinalizedSuccess: SocketReceive = SocketReceive(
        "topic",
        event = Either.Right(SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED),
        data = mockData,
        version = "1",
        success = true
    )

    val apiError = APIError(ErrorCode.TRANSACTION_CONSUMPTION_EXPIRED, "The transaction has been expired.")
    private val dataTxFinalizedFail: SocketReceive = SocketReceive(
        "topic",
        event = Either.Right(SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED),
        version = "1",
        data = mockData,
        success = false,
        error = apiError
    )

    private val txRequestCb: SocketCustomEventCallback.TransactionRequestCallback = mock()
    private val txConsumptionCb: SocketCustomEventCallback.TransactionConsumptionCallback = mock()

    private lateinit var customEventDispatcher: CustomEventDispatcher

    @Before
    fun setup() {
        customEventDispatcher = CustomEventDispatcher()
    }

    @Test
    fun `handleEvent should call handleTransactionRequestEvent to handle TransactionRequestCallback`() {
        customEventDispatcher.socketReceive = dataTxRequest
        customEventDispatcher.socketCustomEventCallback = txRequestCb

        customEventDispatcher.handleEvent(SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST)

        with(customEventDispatcher) {
            verify(txRequestCb, times(1))
                .handleTransactionRequestEvent(dataTxRequest, SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST)
        }
    }

    @Test
    fun `handleEvent should call handleTransactionConsumptionEvent to handle TransactionConsumptionCallback`() {
        customEventDispatcher.socketReceive = dataTxFinalizedSuccess
        customEventDispatcher.socketCustomEventCallback = txConsumptionCb

        customEventDispatcher.handleEvent(SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED)

        with(customEventDispatcher) {
            verify(txConsumptionCb, times(1))
                .handleTransactionConsumptionEvent(dataTxFinalizedSuccess, SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED)
        }
    }

    @Test
    fun `transactionRequestCallback's onTransactionConsumptionRequest should be invoked correctly`() {
        customEventDispatcher.socketCustomEventCallback = txRequestCb
        with(customEventDispatcher) {
            txRequestCb.handleTransactionRequestEvent(dataTxRequest, SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST)
        }

        verify(txRequestCb, times(1)).onTransactionConsumptionRequest(mockData)
        verifyNoMoreInteractions(txRequestCb)
    }

    @Test
    fun `transactionRequestCallback's onTransactionConsumptionFinalizedSuccess should be invoked correctly`() {
        customEventDispatcher.socketCustomEventCallback = txRequestCb
        with(customEventDispatcher) {
            txRequestCb.handleTransactionRequestEvent(dataTxFinalizedSuccess, SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED)
        }

        verify(txRequestCb, times(1)).onTransactionConsumptionFinalizedSuccess(mockData)
        verifyNoMoreInteractions(txRequestCb)
    }

    @Test
    fun `transactionRequestCallback's onTransactionConsumptionFinalizedFail should be invoked correctly`() {
        customEventDispatcher.socketCustomEventCallback = txRequestCb
        with(customEventDispatcher) {
            txRequestCb.handleTransactionRequestEvent(dataTxFinalizedFail, SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED)
        }

        verify(txRequestCb, times(1)).onTransactionConsumptionFinalizedFail(mockData, apiError)
        verifyNoMoreInteractions(txRequestCb)
    }

    @Test
    fun `transactionConsumptionCallback's onTransactionConsumptionFinalizedSuccess should be invoked correctly`() {
        customEventDispatcher.socketCustomEventCallback = txConsumptionCb
        with(customEventDispatcher) {
            txConsumptionCb.handleTransactionConsumptionEvent(dataTxFinalizedSuccess, SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED)
        }

        verify(txConsumptionCb, times(1)).onTransactionConsumptionFinalizedSuccess(mockData)
        verifyNoMoreInteractions(txConsumptionCb)
    }

    @Test
    fun `transactionConsumptionCallback's onTransactionConsumptionFinalizedFail should be invoked correctly`() {
        customEventDispatcher.socketCustomEventCallback = txConsumptionCb
        with(customEventDispatcher) {
            txConsumptionCb.handleTransactionConsumptionEvent(dataTxFinalizedFail, SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED)
        }

        verify(txConsumptionCb, times(1)).onTransactionConsumptionFinalizedFail(mockData, apiError)
        verifyNoMoreInteractions(txConsumptionCb)
    }
}
