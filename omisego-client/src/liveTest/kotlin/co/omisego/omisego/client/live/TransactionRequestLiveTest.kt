package co.omisego.omisego.client.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.exception.OMGAPIErrorException
import co.omisego.omisego.extension.bd
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.Token
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.model.transaction.consumption.TransactionConsumptionActionParams
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.model.transaction.request.TransactionRequestCreateParams
import co.omisego.omisego.model.transaction.request.TransactionRequestParams
import co.omisego.omisego.model.transaction.request.toTransactionConsumptionParams
import co.omisego.omisego.operation.startListeningEvents
import co.omisego.omisego.websocket.listener.TransactionRequestListener
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.fail

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class TransactionRequestLiveTest : LiveTest() {

    private lateinit var token: Token

    @Before
    fun setup() {
        val setting = client.getSettings().execute()
        token = setting.body()?.data?.tokens?.get(0) ?: fail("There's no tokens available.")
    }

    @Test
    fun `create_transaction_request should return 200 and parsed the response correctly`() {
        val params = TransactionRequestCreateParams(tokenId = token.id)
        val response = client.createTransactionRequest(params).execute()
        response.isSuccessful shouldBe true
        response.body()?.data shouldBeInstanceOf TransactionRequest::class.java
    }

    @Test
    fun `get_transaction_request should be able to get a created transaction request successfully`() {
        val params = TransactionRequestCreateParams(tokenId = token.id)
        val response = client.createTransactionRequest(params).execute()
        val createdTransactionRequest = response.body()?.data
        val formattedId = createdTransactionRequest?.formattedId ?: fail("Cannot create a transaction request")

        val transactionResponse = client.retrieveTransactionRequest(TransactionRequestParams(formattedId)).execute()
        transactionResponse.isSuccessful shouldBe true
        val retrievedTransactionRequest = transactionResponse.body()?.data
        retrievedTransactionRequest shouldEqual createdTransactionRequest
    }

    /**
     * This test aims to test the full flow of a transfer between users including websocket integration
     * The problem here is that (for obvious reasons) the eWallet doesn't allow a transfer between 2 same addresses,
     * this means that we would require to setup the live tests using 2 different authentication tokens for the purpose of this test.
     * Even if this is doable, we would face an other issue because the user would possibly not have enough balance to make the transfer
     * so the test would fail.
     * So it has been decided, for now, that this test will only scope the following:
     * 1) Generate the transaction request.
     * 2) Subscribe to events on this transaction request (listen for consumptions request).
     * 3) Try to consume the transaction request.
     * 4) Wait for the consumption request via the websockets.
     * 5) Confirm the consumption request
     * 6) Assert a same_address error as we did all actions with the same balance
     */

    @Test
    fun `create_transaction_request with requireConfirmation then consume_transaction_request should receive same_address error`() {
        /* Create a transaction request */
        val params = TransactionRequestCreateParams(tokenId = token.id)
        val response = client.createTransactionRequest(params).execute()
        val createdTransactionRequest = response.body()?.data ?: fail("Cannot create transaction request.")

        println("Create a transaction id ${createdTransactionRequest.id}")

        /* The requestor is listening to the socket events. */
        createdTransactionRequest.startListeningEvents(socketClient, listener = object : TransactionRequestListener() {
            override fun onTransactionConsumptionRequest(transactionConsumption: TransactionConsumption) {
                try {
                    /* Approve the transaction consumption (should throw OMGAPIErrorException.) */
                    client.approveTransactionConsumption(TransactionConsumptionActionParams(transactionConsumption.id)).execute()

                    /* The exception should be already thrown in the line before. So if it reach this line, it should be fail. */
                    fail("It should throw OMGAPIErrorException with transaction:same_address error.")
                } catch (e: Exception) {
                    e shouldBeInstanceOf OMGAPIErrorException::class.java

                    /* The error code should be transaction:same_address */
                    (e as OMGAPIErrorException).response.data.code shouldEqual ErrorCode.TRANSACTION_SAME_ADDRESS
                }
            }

            override fun onTransactionConsumptionFinalizedSuccess(transactionConsumption: TransactionConsumption) {
                fail("The transaction consumption should not be finalized.")
            }

            override fun onTransactionConsumptionFinalizedFail(transactionConsumption: TransactionConsumption, apiError: APIError) {
                apiError.code shouldEqual ErrorCode.TRANSACTION_SAME_ADDRESS
            }
        })

        /* The consumer consume the transaction request. */
        val transactionConsumptionResponse = client.consumeTransactionRequest(
            createdTransactionRequest.toTransactionConsumptionParams(amount = 500.bd)
        ).execute()

        val transactionConsumption = transactionConsumptionResponse.body()?.data
            ?: fail("Cannot consume a transaction.")

        println("Consume a transaction id ${transactionConsumption.transactionRequest.id}")

        /* Wait for websocket event */
        Thread.sleep(3_000)
    }
}
