package co.omisego.omisego.live.auth

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.extension.bd
import co.omisego.omisego.live.BaseAuthTest
import co.omisego.omisego.model.Account
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.TransactionConsumption
import co.omisego.omisego.model.TransactionConsumptionStatus
import co.omisego.omisego.model.TransactionRequest
import co.omisego.omisego.model.TransactionRequestType
import co.omisego.omisego.model.approve
import co.omisego.omisego.model.params.TransactionRequestParams
import co.omisego.omisego.model.params.admin.TransactionConsumptionParams
import co.omisego.omisego.model.params.admin.TransactionRequestCreateParams
import co.omisego.omisego.model.reject
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldStartWith
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class TransactionRequestLiveTest : BaseAuthTest() {
    val params by lazy {
        TransactionRequestCreateParams(
            type = TransactionRequestType.RECEIVE,
            accountId = testMasterAccount.id,
            amount = null,
            tokenId = testTokenId,
            allowAmountOverride = true,
            requireConfirmation = false
        )
    }
    private val testExpectedAmount: BigDecimal by lazy { 1.bd }

    private val testTransactionRequestTypeReceive by lazy {
        createTransactionRequest(params)
    }
    private val testTransactionRequestTypeSend by lazy {
        createTransactionRequest(params.copy(type = TransactionRequestType.SEND))
    }
    private val testTransactionRequestTypeReceiveRequiredConfirmation by lazy {
        createTransactionRequest(params.copy(requireConfirmation = true))
    }

    @Test
    fun `getTransactionRequest should return the corresponding transaction request correctly`() {
        val response = client.getTransactionRequest(TransactionRequestParams(testTransactionRequestTypeReceive.id)).execute()
        response.isSuccessful shouldBe true
        response.body()?.data shouldBeInstanceOf TransactionRequest::class.java
        response.body()?.data?.formattedId shouldEqual testTransactionRequestTypeReceive.formattedId
    }

    @Test
    fun `another account consume master account's transaction request with type RECEIVE should have less token correctly`() {
        /* Prepare test data */
        val params = createTransactionConsumptionParams(testTransactionRequestTypeReceive.id)

        val amountBeforeConsume = testBrandWallet.balances.find { it.token.id == testTokenId }!!.amount

        /* Action */
        val response = client.consumeTransactionRequest(params).execute()

        /* Assert */
        response.verify(
            expectedStatus = TransactionConsumptionStatus.CONFIRMED,
            expectedBalanceAmount = amountBeforeConsume.minus(testExpectedAmount)
        )
    }

    @Test
    fun `another account consume master account's transaction request with type SEND should have more token`() {
        /* Prepare test data */
        val params = createTransactionConsumptionParams(testTransactionRequestTypeSend.id)

        val amountBeforeConsume = testBrandWallet.balances.find { it.token.id == testTokenId }!!.amount

        /* Action */
        val response = client.consumeTransactionRequest(params).execute()

        /* Assert */
        response.verify(
            expectedStatus = TransactionConsumptionStatus.CONFIRMED,
            expectedBalanceAmount = amountBeforeConsume.plus(testExpectedAmount)
        )
    }

    @Test
    fun `another account consume master account's a confirmation-required transaction request with type RECEIVE, the status should be pending`() {
        /* Prepare test data */
        val params = createTransactionConsumptionParams(testTransactionRequestTypeReceiveRequiredConfirmation.id)

        val amountBeforeConsume = testBrandWallet.balances.find { it.token.id == testTokenId }!!.amount

        /* Action */
        val response = client.consumeTransactionRequest(params).execute()

        /* Assert */
        response.verify(
            expectedStatus = TransactionConsumptionStatus.PENDING,
            expectedBalanceAmount = amountBeforeConsume
        )
    }

    @Test
    fun `create transaction request with different types should return a valid transaction request`() {
        testTransactionRequestTypeReceive.id shouldStartWith "txr"
        testTransactionRequestTypeReceiveRequiredConfirmation.id shouldStartWith "txr"
        testTransactionRequestTypeSend.id shouldStartWith "txr"

        println(
            """
                receive, not required confirmation -> $testTransactionRequestTypeReceive
                send, not required confirmation -> $testTransactionRequestTypeSend
                receive, required confirmation -> $testTransactionRequestTypeReceiveRequiredConfirmation
            """.trimIndent()
        )
    }

    @Test
    fun `approve transaction consumption should return an confirmed-status transaction consumption`() {
        /* Prepare test data */
        val requiredConfirmationTransactionRequest = createTransactionRequest(params.copy(requireConfirmation = true))
        val transactionConsumptionParams = createTransactionConsumptionParams(requiredConfirmationTransactionRequest.id)
        val amountBeforeConsume = testBrandWallet.balances.find { it.token.id == testTokenId }!!.amount
        val responsePendingTransactionConsumption = client.consumeTransactionRequest(transactionConsumptionParams).execute()

        /* Verify pending transaction consumption */
        responsePendingTransactionConsumption.verify(
            expectedStatus = TransactionConsumptionStatus.PENDING,
            expectedBalanceAmount = amountBeforeConsume
        )

        /* Action */
        val response = responsePendingTransactionConsumption.body()?.data?.approve(client)?.execute()

        /* Verify confirm transaction consumption */
        response?.isSuccessful shouldBe true
        response?.verify(
            expectedStatus = TransactionConsumptionStatus.CONFIRMED,
            expectedBalanceAmount = amountBeforeConsume.minus(testExpectedAmount)
        )
    }

    @Test
    fun `reject transaction consumption should return an rejected-status transaction consumption`() {
        /* Prepare test data */
        val requiredConfirmationTransactionRequest = createTransactionRequest(params.copy(requireConfirmation = true))
        val transactionConsumptionParams = createTransactionConsumptionParams(requiredConfirmationTransactionRequest.id)
        val amountBeforeConsume = testBrandWallet.balances.find { it.token.id == testTokenId }!!.amount
        val responsePendingTransactionConsumption = client.consumeTransactionRequest(transactionConsumptionParams).execute()

        /* Verify pending transaction consumption */
        responsePendingTransactionConsumption.verify(
            expectedStatus = TransactionConsumptionStatus.PENDING,
            expectedBalanceAmount = amountBeforeConsume
        )

        /* Action */
        val response = responsePendingTransactionConsumption.body()?.data?.reject(client)?.execute()

        /* Verify confirm transaction consumption */
        response?.isSuccessful shouldBe true
        response?.verify(
            expectedStatus = TransactionConsumptionStatus.REJECTED,
            expectedBalanceAmount = amountBeforeConsume
        )
    }

    private fun createTransactionConsumptionParams(formattedId: String): TransactionConsumptionParams {
        return TransactionConsumptionParams.create(
            formattedId,
            address = testBrandWallet.address,
            amount = testExpectedAmount,
            tokenId = testTokenId
        )
    }

    private fun Response<OMGResponse<TransactionConsumption>>.verify(
        expectedStatus: TransactionConsumptionStatus = TransactionConsumptionStatus.CONFIRMED,
        consumerAccount: Account = testBrandAccount,
        expectedBalanceAmount: BigDecimal
    ) {
        isSuccessful shouldBe true
        val transactionConsumption = body()?.data!!
        transactionConsumption shouldBeInstanceOf TransactionConsumption::class.java
        with(transactionConsumption) {
            token.id shouldEqual testTokenId
            estimatedRequestAmount shouldEqual testExpectedAmount
            estimatedConsumptionAmount shouldEqual testExpectedAmount
            status shouldBe expectedStatus
            consumerAccount.getWallet().balances.find { it.token.id == testTokenId }?.amount shouldEqual expectedBalanceAmount
        }
    }
}
