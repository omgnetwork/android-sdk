package co.omisego.omisego.live.auth

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.live.BaseAuthTest
import co.omisego.omisego.model.pagination.Filter
import co.omisego.omisego.model.params.admin.TransactionListParams
import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class FilteringTest : BaseAuthTest() {

    @Test
    fun `test match_any should be able to filter correctly`() {
        val response = client.getTransactions(TransactionListParams.create(
            perPage = 10,
            matchAny = listOf(
                Filter("id", "eq", "txn_01cwtfqywgk3nnt8r1w38wvfxf"),
                Filter("id", "eq", "txn_01cwtfqg3djf906v4fasxrqe0s")
            ))
        ).execute()

        println(response.body()?.data?.data?.map { it.id })

        response.body()?.data?.data?.count { it.id == "txn_01cwtfqywgk3nnt8r1w38wvfxf" } shouldEqual 1
        response.body()?.data?.data?.count { it.id == "txn_01cwtfqg3djf906v4fasxrqe0s" } shouldEqual 1
    }

    @Test
    fun `test match_all should be able to filter correctly`() {
        val response = client.getTransactions(TransactionListParams.create(
            perPage = 10,
            matchAll = listOf(
                Filter("id", "eq", "txn_01cwtfqywgk3nnt8r1w38wvfxf"),
                Filter("id", "eq", "txn_01cwtfqg3djf906v4fasxrqe0s")
            ))
        ).execute()

        println(response.body()?.data?.data?.map { it.id })

        response.body()?.data?.data?.size shouldEqual 0
    }
}
