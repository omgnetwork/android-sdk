package co.omisego.omisego.model.transaction

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/6/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.extension.bd
import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.Token
import co.omisego.omisego.model.TransactionSource
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class TransactionSourceTest : GsonDelegator() {
    private val transactionSourceFile by ResourceFile("transaction_source.json", "object")
    private val transactionSource by lazy { gson.fromJson(transactionSourceFile.readText(), TransactionSource::class.java) }

    @Test
    fun `transaction should be parsed correctly`() {
        with(transactionSource) {
            address shouldEqualTo "2e3982f5-4a27-498d-a91b-7bb2e2a8d3d1"
            amount shouldEqual 1000.bd
            token shouldBeInstanceOf Token::class.java
        }
    }
}
