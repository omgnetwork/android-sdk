package co.omisego.omisego.model.transaction.consumption

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 26/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.extension.bd
import co.omisego.omisego.model.transaction.request.TransactionRequest
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Test

class TransactionConsumptionParamsTest {

    @Test
    fun `TransactionConsumptionParams init should be return null if the transactionRequest amount is null`() {
        val transactionRequest: TransactionRequest = mock()

        whenever(transactionRequest.id).thenReturn("omg-test1234")
        whenever(transactionRequest.amount).thenReturn(null)

        val tx = TransactionConsumptionParams.init(transactionRequest)
        tx shouldBe null
    }

    @Test
    fun `TransactionConsumptionParams init should be use amount null if transactionRequest amount equals the amount`() {
        val transactionRequest: TransactionRequest = mock()

        whenever(transactionRequest.id).thenReturn("omg-test1234")
        whenever(transactionRequest.amount).thenReturn(1234.bd)

        val tx = TransactionConsumptionParams.init(transactionRequest, amount = 1234.bd)
        tx?.amount shouldBe null
    }

    @Test
    fun `TransactionConsumptionParams init should use amount as the sending amount if transactionRequest amount and the sending amount are not the same`() {
        val transactionRequest: TransactionRequest = mock()

        whenever(transactionRequest.id).thenReturn("omg-test1234")
        whenever(transactionRequest.amount).thenReturn(1234.bd)

        val tx = TransactionConsumptionParams.init(transactionRequest, amount = 100.bd)
        tx?.amount shouldEqual 100.bd
    }

    @Test
    fun `TransactionConsumptionParams call init function multiple times should produce unique idempotencyToken`() {
        val transactionRequest: TransactionRequest = mock()

        whenever(transactionRequest.id).thenReturn("omg-test1234")
        whenever(transactionRequest.amount).thenReturn(1234.bd)

        val idempotencyTokenSet = mutableSetOf<String>()

        for (i in 0 until 1000) {
            idempotencyTokenSet.add(TransactionConsumptionParams.init(transactionRequest)?.idempotencyToken ?: "")
        }

        idempotencyTokenSet.size shouldEqual 1000
    }
}
