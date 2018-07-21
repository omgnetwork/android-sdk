package co.omisego.omisego.operation

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 21/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.User
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.websocket.SocketClientContract
import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.listener.ListenableTopicListener
import co.omisego.omisego.websocket.listener.TransactionConsumptionTopicListener
import co.omisego.omisego.websocket.listener.TransactionRequestTopicListener
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import org.junit.Test

class ListenableTest {
    private val mockClient: SocketClientContract.Client = org.amshove.kluent.mock()
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
    fun `when the user call startListenerEvents, the client should add the listener and join channel correctly`() {
        mockUser.startListeningEvents(
            mockClient,
            listener = object : ListenableTopicListener(mockUser) {
                override fun onSpecificEvent(event: SocketEvent<*>) {
                }
            }
        )

        verify(mockClient, times(1)).addCustomEventListener(any<ListenableTopicListener>())
        verify(mockClient, times(1)).joinChannel(SocketTopic("user"))
        verifyNoMoreInteractions(mockClient)
    }

    @Test
    fun `when the transaction_request call startListenerEvents, the client should add the listener and join channel correctly`() {
        mockTransactionRequest.startListeningEvents(
            mockClient,
            listener = object : TransactionRequestTopicListener(mockTransactionRequest) {
                override fun onTransactionConsumptionRequest(transactionConsumption: TransactionConsumption) {
                }

                override fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption) {
                }

                override fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError) {
                }
            }
        )

        verify(mockClient, times(1)).addCustomEventListener(any<TransactionRequestTopicListener>())
        verify(mockClient, times(1)).joinChannel(SocketTopic("transaction_request"))
        verifyNoMoreInteractions(mockClient)
    }

    @Test
    fun `when the transaction_consumption call startListenerEvents, the client should add the listener and join channel correctly`() {
        mockTransactionConsumption.startListeningEvents(
            mockClient,
            listener = object : TransactionConsumptionTopicListener(mockTransactionConsumption) {
                override fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption) {
                }

                override fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError) {
                }
            }
        )

        verify(mockClient, times(1)).addCustomEventListener(any<TransactionConsumptionTopicListener>())
        verify(mockClient, times(1)).joinChannel(SocketTopic("transaction_consumption"))
        verifyNoMoreInteractions(mockClient)
    }
}
