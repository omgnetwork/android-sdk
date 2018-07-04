package co.omisego.omisego.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.transaction.list.TransactionListParams
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class TransactionLiveTest : LiveTest() {

    @Test
    fun `get_transactions should return 200 and parsed the response correctly`() {
        val transactions = client.getTransactions(TransactionListParams.create(searchTerm = null)).execute()
        transactions.isSuccessful shouldBe true
        transactions.body()?.data shouldNotBe null
        with(transactions.body()?.data!!) {
            this.pagination shouldBeInstanceOf co.omisego.omisego.model.pagination.Pagination::class.java
            this.data shouldBeInstanceOf kotlin.collections.List::class.java
            if (this.data.isNotEmpty()) {
                this.data[0] shouldBeInstanceOf co.omisego.omisego.model.transaction.Transaction::class.java
            }
        }
    }
}
