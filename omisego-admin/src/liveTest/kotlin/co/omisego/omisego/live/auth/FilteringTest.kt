package co.omisego.omisego.live.auth

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.live.BaseAuthTest
import co.omisego.omisego.model.filterable.Filterable
import co.omisego.omisego.model.filterable.buildFilterList
import co.omisego.omisego.model.params.admin.TransactionListParams
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class FilteringTest : BaseAuthTest() {

    @Test
    fun `test match_any should be able to filter correctly`() {
        val testTransactions = client.getTransactions(TransactionListParams.create()).execute().body()?.data?.data
        val txId1 = testTransactions?.first()?.id
        val txId2 = testTransactions?.last()?.id

        val response = client.getTransactions(TransactionListParams.create(
            perPage = 10,
            matchAny = buildFilterList<Filterable.TransactionFields> { field ->
                add(field.id eq txId1!!)
                add(field.id eq txId2!!)
            })
        ).execute()

        response.body()?.data?.data?.size shouldBe 2
        response.body()?.data?.data?.find { it.id == txId1!! } shouldNotBe null
        response.body()?.data?.data?.find { it.id == txId2 } shouldNotBe null
    }

    @Test
    fun `test match_all should be able to filter correctly`() {
        val testTransactions = client.getTransactions(TransactionListParams.create()).execute().body()?.data?.data
        val txId1 = testTransactions?.first()?.id
        val txId2 = testTransactions?.last()?.id

        val response = client.getTransactions(TransactionListParams.create(
            perPage = 10,
            matchAll = buildFilterList<Filterable.TransactionFields> { field ->
                add(field.id eq txId1!!)
                add(field.id eq txId2!!)
            })
        ).execute()
        println(response.body()?.data?.data?.map { it.id })

        response.body()?.data?.data?.size shouldEqual 0
    }
}
