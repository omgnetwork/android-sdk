package co.omisego.omisego.model.transaction

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/6/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.pagination.Paginable
import co.omisego.omisego.utils.DateConverter
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class TransactionTest : GsonDelegator() {
    private val transactionFile by ResourceFile("transaction.json", "object")
    private val transaction by lazy { gson.fromJson(transactionFile.readText(), Transaction::class.java) }
    private val dateConverter by lazy { DateConverter() }

    @Test
    fun `transaction should be parsed correctly`() {
        with(transaction) {
            id shouldEqualTo "ce3982f5-4a27-498d-a91b-7bb2e2a8d3d1"
            from shouldBeInstanceOf TransactionSource::class.java
            to shouldBeInstanceOf TransactionSource::class.java
            exchange shouldBeInstanceOf TransactionExchange::class.java
            status shouldEqual Paginable.Transaction.TransactionStatus.CONFIRMED
            metadata shouldEqual mapOf<String, Any>()
            metadata shouldEqual mapOf<String, Any>()
            createdAt shouldEqual dateConverter.fromString("2018-01-01T00:00:00Z")
        }
    }
}
