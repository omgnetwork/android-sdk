package co.omisego.omisego.admin.model.params.admin

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 26/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.admin.utils.GsonDelegator
import co.omisego.omisego.extension.bd
import co.omisego.omisego.model.Token
import co.omisego.omisego.model.TransactionRequest
import co.omisego.omisego.model.params.admin.TransactionConsumptionParams
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.Test

class TransactionConsumptionParamsTest : GsonDelegator() {

    @Test
    fun `TransactionConsumptionParams should be return null if the transactionRequest amount is null`() {
        val transactionRequest: TransactionRequest = mock()

        whenever(transactionRequest.formattedId).thenReturn("omg-test1234")
        whenever(transactionRequest.amount).thenReturn(null)

        val result = { TransactionConsumptionParams.create(transactionRequest) }

        result shouldThrow IllegalArgumentException::class withMessage
            "The transactionRequest amount or the amount of token to createTransaction should be provided"
    }

    @Test
    fun `TransactionConsumptionParams should have amount equals null if the transaction_request_amount and amount are the same`() {
        val transactionRequest: TransactionRequest = mock()
        val token: Token = mock()

        whenever(transactionRequest.formattedId).thenReturn("omg-test1234")
        whenever(transactionRequest.amount).thenReturn("567".toBigDecimal())
        whenever(transactionRequest.token).thenReturn(token)
        whenever(token.subunitToUnit).thenReturn(100.bd)

        val result = TransactionConsumptionParams.create(transactionRequest, amount = "567.0000000000".toBigDecimal())

        result.amount shouldBe null
    }

    @Test
    fun `TransactionConsumptionParams should be use amount null if transactionRequest amount equals the amount`() {
        val transactionRequest: TransactionRequest = mock()

        whenever(transactionRequest.formattedId).thenReturn("omg-test1234")
        whenever(transactionRequest.amount).thenReturn(1234.bd)

        val tx = TransactionConsumptionParams.create(transactionRequest, amount = 1234.bd)
        tx.amount shouldBe null
    }

    @Test
    fun `TransactionConsumptionParams should use amount as the sending amount if transactionRequest amount and the sending amount are not the same`() {
        val transactionRequest: TransactionRequest = mock()

        whenever(transactionRequest.formattedId).thenReturn("omg-test1234")
        whenever(transactionRequest.amount).thenReturn(1234.bd)

        val tx = TransactionConsumptionParams.create(transactionRequest, amount = 100.bd)
        tx.amount shouldEqual 100.bd
    }

    @Test
    fun `TransactionConsumptionParams call function multiple times should produce unique idempotencyToken`() {
        val transactionRequest: TransactionRequest = mock()

        whenever(transactionRequest.formattedId).thenReturn("omg-test1234")
        whenever(transactionRequest.amount).thenReturn(1234.bd)

        val idempotencyTokenSet = mutableSetOf<String>()

        for (i in 0 until 1000) {
            idempotencyTokenSet.add(TransactionConsumptionParams.create(transactionRequest).idempotencyToken)
        }

        idempotencyTokenSet.size shouldEqual 1000
    }

    @Test
    fun `TransactionConsumptionParams should be created correctly`() {
        val expectedJson = """
            {
              "formatted_transaction_request_id": "fmt:txr_id",
              "amount": 1,
              "address": null,
              "idempotency_token": "fmt:txr_id-55366101236611",
              "correlation_id": null,
              "metadata": {},
              "encrypted_metadata": {},
              "account_id": "account_id",
              "user_id": "user_id",
              "provider_user_id": null,
              "token_id": null,
              "exchange_account_id": "exchange_account_id",
              "exchange_wallet_address": "exchange_wallet_address"
            }
            """

        val params = TransactionConsumptionParams.create(
            "fmt:txr_id",
            amount = 1.bd,
            exchangeAccountId = "exchange_account_id",
            exchangeWalletAddress = "exchange_wallet_address",
            userId = "user_id",
            accountId = "account_id"
        )

        val expected = gson.fromJson(expectedJson, TransactionConsumptionParams::class.java)
        params.copy(idempotencyToken = "") shouldEqual expected.copy(idempotencyToken = "")
    }
}
