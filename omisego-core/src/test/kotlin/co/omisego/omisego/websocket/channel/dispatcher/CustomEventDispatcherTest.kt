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
import co.omisego.omisego.model.TransactionConsumption
import co.omisego.omisego.utils.Either
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionFinalizedEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionRequestEvent
import co.omisego.omisego.websocket.listener.SocketChannelListener
import co.omisego.omisego.websocket.listener.TransactionConsumptionListener
import co.omisego.omisego.websocket.listener.TransactionRequestListener
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import org.amshove.kluent.mock
import org.junit.Before
import org.junit.Test

class CustomEventDispatcherTest {

    private val mockData: TransactionConsumption = mock()
    private val dataTxRequest: SocketReceive<TransactionConsumption> = SocketReceive(
        "transaction_request",
        event = Either.Right(SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST),
        data = mockData,
        version = "1",
        success = true
    )

    private val dataTxFinalizedSuccess: SocketReceive<TransactionConsumption> = SocketReceive(
        "transaction_consumption",
        event = Either.Right(SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED),
        data = mockData,
        version = "1",
        success = true
    )

    val apiError = APIError(ErrorCode.TRANSACTION_CONSUMPTION_EXPIRED, "The transaction has been expired.")

    private val dataTxFinalizedFail: SocketReceive<TransactionConsumption> = SocketReceive(
        "transaction_consumption",
        event = Either.Right(SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED),
        version = "1",
        data = mockData,
        success = false,
        error = apiError
    )

    private val txRequestCb: TransactionRequestListener = mock()
    private val txConsumptionCb: TransactionConsumptionListener = mock()
    private val mockSocketChannelListener: SocketChannelListener = mock()

    private lateinit var customEventDispatcher: CustomEventDispatcher

    @Before
    fun setup() {
        customEventDispatcher = CustomEventDispatcher(mockSocketChannelListener)
    }

    @Test
    fun `handleEvent should invoke onEvent with TransactionConsumptionRequestEvent`() {
        customEventDispatcher.customEventListeners.add(txRequestCb)

        customEventDispatcher.handleEvent(SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST, dataTxRequest)

        with(customEventDispatcher) {
            verify(txRequestCb, times(1))
                .onEvent(TransactionConsumptionRequestEvent(dataTxRequest))
        }
    }

    @Test
    fun `handleEvent should invoke onEvent with TransactionConsumptionFinalizedEvent`() {
        customEventDispatcher.customEventListeners.add(txConsumptionCb)

        customEventDispatcher.handleEvent(SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED, dataTxFinalizedSuccess)

        with(customEventDispatcher) {
            verify(txConsumptionCb, times(1))
                .onEvent(TransactionConsumptionFinalizedEvent(dataTxFinalizedSuccess))
        }
    }

    @Test
    fun `handleEvent should invoke onError of SocketChannelListener class when event is not exist`() {
        // Add all possible custom listeners.
        customEventDispatcher.customEventListeners.add(txRequestCb)
        customEventDispatcher.customEventListeners.add(txConsumptionCb)

        // Unknown custom event is sent to handleEvent.
        customEventDispatcher.handleEvent(SocketCustomEvent.OTHER, dataTxFinalizedFail)

        // channelListener's onError function should be invoked.
        verify(mockSocketChannelListener, times(1)).onError(dataTxFinalizedFail.error!!)

        // All known socket event callback should not be invoked.
        verifyZeroInteractions(txConsumptionCb)
        verifyZeroInteractions(txRequestCb)
    }

    @Test
    fun `handleEvent should invoke all callbacks`() {
        customEventDispatcher.customEventListeners.add(txRequestCb)
        customEventDispatcher.customEventListeners.add(txConsumptionCb)

        customEventDispatcher.handleEvent(SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST, dataTxFinalizedSuccess)

        verify(txRequestCb, times(1)).onEvent(TransactionConsumptionRequestEvent(dataTxFinalizedSuccess))
        verify(txConsumptionCb, times(1)).onEvent(TransactionConsumptionRequestEvent(dataTxFinalizedSuccess))
    }
}
