package co.omisego.omisego.model.transaction

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/6/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.Transaction
import co.omisego.omisego.model.TransactionExchange
import co.omisego.omisego.model.TransactionSource
import co.omisego.omisego.model.pagination.Paginable
import co.omisego.omisego.utils.DateConverter
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class TransactionTest : GsonDelegator() {
    private val dateConverter by lazy { DateConverter() }
    private val transactionWithErrorFile by ResourceFile("transaction.json", "object")
    private val transactionWithoutErrorFile by ResourceFile("transaction_without_error.json", "object")
    private val transactionWithError by lazy { gson.fromJson(transactionWithErrorFile.readText(), Transaction::class.java) }
    private val transactionWithoutError by lazy { gson.fromJson(transactionWithoutErrorFile.readText(), Transaction::class.java) }

    @Test
    fun `transaction with error_code should be parsed correctly`() {
        with(transactionWithError) {
            id shouldEqualTo "ce3982f5-4a27-498d-a91b-7bb2e2a8d3d1"
            from shouldBeInstanceOf TransactionSource::class.java
            to shouldBeInstanceOf TransactionSource::class.java
            exchange shouldBeInstanceOf TransactionExchange::class.java
            status shouldEqual Paginable.Transaction.TransactionStatus.CONFIRMED
            metadata shouldEqual mapOf<String, Any>()
            metadata shouldEqual mapOf<String, Any>()
            createdAt shouldEqual dateConverter.fromString("2018-01-01T00:00:00Z")
            error shouldEqual APIError(
                ErrorCode.TRANSACTION_UNAUTHORIZED_AMOUNT_OVERRIDE,
                "The amount for this transaction request cannot be overridden."
            )
        }
    }

    @Test
    fun `transaction without error_code should be parsed correctly`() {
        with(transactionWithoutError) {
            id shouldEqualTo "ce3982f5-4a27-498d-a91b-7bb2e2a8d3d1"
            from shouldBeInstanceOf TransactionSource::class.java
            to shouldBeInstanceOf TransactionSource::class.java
            exchange shouldBeInstanceOf TransactionExchange::class.java
            status shouldEqual Paginable.Transaction.TransactionStatus.CONFIRMED
            metadata shouldEqual mapOf<String, Any>()
            metadata shouldEqual mapOf<String, Any>()
            createdAt shouldEqual dateConverter.fromString("2018-01-01T00:00:00Z")
            error shouldBe null
        }
    }
}
