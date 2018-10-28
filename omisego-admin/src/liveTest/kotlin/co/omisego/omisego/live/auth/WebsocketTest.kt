package co.omisego.omisego.live.auth

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/10/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.extension.bd
import co.omisego.omisego.live.BaseAuthTest
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.TransactionConsumption
import co.omisego.omisego.model.TransactionConsumptionStatus
import co.omisego.omisego.model.TransactionRequestType
import co.omisego.omisego.model.approve
import co.omisego.omisego.model.params.admin.TransactionConsumptionParams
import co.omisego.omisego.model.params.admin.TransactionRequestCreateParams
import co.omisego.omisego.operation.startListeningEvents
import co.omisego.omisego.websocket.OMGSocketClient
import co.omisego.omisego.websocket.SocketClientContract
import co.omisego.omisego.websocket.event.TransactionConsumptionRequestEvent
import co.omisego.omisego.websocket.listener.SocketChannelListener
import co.omisego.omisego.websocket.listener.SocketConnectionListener
import co.omisego.omisego.websocket.listener.SocketCustomEventListener
import co.omisego.omisego.websocket.listener.TransactionConsumptionListener
import co.omisego.omisego.websocket.listener.TransactionRequestListener
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.timeout
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.math.BigDecimal
import java.util.concurrent.Executor

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class WebsocketTest : BaseAuthTest() {

    /* Test data */
    private val params by lazy {
        TransactionRequestCreateParams(
            type = TransactionRequestType.RECEIVE,
            accountId = testMasterAccount.id,
            amount = null,
            tokenId = testTokenId,
            allowAmountOverride = true,
            requireConfirmation = true
        )
    }
    private val testExpectedAmount: BigDecimal by lazy { 1.bd }
    private val mockSocketConnectionListener: SocketConnectionListener = mock()
    private val mockSocketChannelListener: SocketChannelListener = mock()

    /* WebSocket client */
    private val socketClient: SocketClientContract.Client by lazy {
        OMGSocketClient.Builder {
            clientConfiguration = config.copy(
                baseURL = secret.getString("socket_base_url"),
                authenticationToken = testAdminAuthenticationToken.authenticationToken,
                userId = testAdminAuthenticationToken.userId
            )
            executor = Executor { it.run() }
        }.build()
    }

    @Before
    fun setupWebsocket() {
        socketClient.cancel()
        socketClient.addConnectionListener(mockSocketConnectionListener)
        socketClient.addChannelListener(mockSocketChannelListener)
    }

    @After
    fun clearWebsocket() {
        socketClient.removeChannelListener(mockSocketChannelListener)
        socketClient.removeConnectionListener(mockSocketConnectionListener)
        socketClient.cancel()
    }

    @Test
    fun `test the websocket can be opened and closed correctly`() {
        testMasterAccount.startListeningEvents(socketClient, listener = mock())

        verify(mockSocketConnectionListener, timeout(5000)).onConnected()

        /* Wait a bit before leave the channel */
        Thread.sleep(2000)
        testMasterAccount.stopListening(socketClient)

        verify(mockSocketConnectionListener, timeout(5000)).onDisconnected(null)
    }

    @Test
    fun `test the websocket channel can be joined and leaved correctly`() {
        testMasterAccount.startListeningEvents(socketClient, listener = mock())

        verify(mockSocketChannelListener, timeout(5000)).onJoinedChannel(testMasterAccount.socketTopic.name)

        testMasterAccount.stopListening(socketClient)

        verify(mockSocketChannelListener, timeout(5000)).onLeftChannel(testMasterAccount.socketTopic.name)
    }

    @Test
    fun `test the account should receive the websocket event correctly`() {
        /* --- Prepare test data --- */
        val mockFinalizedEventCallback: SocketCustomEventListener = mock()

        /* Create Transaction Request */
        val testTransactionRequest = createTransactionRequest(params)

        /* Create Transaction Consumption Params */
        val consumptionParams = createTransactionConsumptionParams(testTransactionRequest.id)

        /* --- Action --- */
        /* Start listening to the transaction consumption finalized event */
        testMasterAccount.startListeningEvents(socketClient, listener = mockFinalizedEventCallback)

        /* Consume the transaction request */
        val response = client.consumeTransactionRequest(consumptionParams).execute()

        /* Verify */
        response.body()?.data?.status shouldEqual TransactionConsumptionStatus.PENDING
        verify(mockFinalizedEventCallback, timeout(5000)).onEvent(any(TransactionConsumptionRequestEvent::class))
        verifyNoMoreInteractions(mockFinalizedEventCallback)
    }

    @Test
    fun `test the transaction consumption can be used for listening to the websocket event correctly`() {
        /* --- Prepare test data --- */

        /* Create mock listener for verify that it's invoked */
        val listener: TransactionConsumptionListener = spy(object : TransactionConsumptionListener() {
            override fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption) {}
            override fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError) {}
        })

        val testTransactionRequest = createTransactionRequest(params)

        /* Create transaction consumption params */
        val consumptionParams = createTransactionConsumptionParams(testTransactionRequest.id)

        /* --- Action --- */

        /* Consume the transaction request  */
        val response = client.consumeTransactionRequest(consumptionParams).execute()

        /* Get its data */
        val transactionConsumption = response.body()?.data

        /* Listening to the event */
        transactionConsumption?.startListeningEvents(socketClient, listener = listener)

        /* Verify the transaction consumption status is pending*/
        response.body()?.data?.status shouldEqual TransactionConsumptionStatus.PENDING

        Thread.sleep(300)

        /* Approve the transaction consumption */
        transactionConsumption?.approve(client)?.execute()

        /* Verify onTransactionConsumptionFinalizedSuccess is called */
        verify(listener, timeout(5000)).onTransactionConsumptionFinalizedSuccess(any())
    }

    @Test
    fun `test the transaction request can be used for listening to the websocket event correctly`() {
        /* --- Prepare the test data --- */

        /* Create mock listener for verify that it's invoked */
        val listener: TransactionRequestListener = spy(object : TransactionRequestListener() {
            override fun onTransactionConsumptionRequest(transactionConsumption: TransactionConsumption) {}
            override fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption) {}
            override fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError) {}
        })

        /* Create Transaction Request */
        val testTransactionRequest = createTransactionRequest(params)

        /* Start listening to the socket event */
        testTransactionRequest.startListeningEvents(socketClient, listener = listener)

        /* Create transaction consumption params */
        val consumptionParams = createTransactionConsumptionParams(testTransactionRequest.id)

        /* Consume a transaction request */
        client.consumeTransactionRequest(consumptionParams).execute()

        /* Verify onTransactionConsumptionRequest is called */
        verify(listener, timeout(5000)).onTransactionConsumptionRequest(any())
    }

    private fun createTransactionConsumptionParams(formattedId: String): TransactionConsumptionParams {
        return TransactionConsumptionParams.create(
            formattedId,
            address = testBrandWallet.address,
            amount = testExpectedAmount,
            tokenId = testTokenId
        )
    }
}
