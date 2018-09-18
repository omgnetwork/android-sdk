package co.omisego.omisego.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.LiveTest
import co.omisego.omisego.model.params.LoginParams
import co.omisego.omisego.model.transaction.list.TransactionListParams
import org.amshove.kluent.shouldBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class ListTransactionLiveTest : LiveTest() {
    private val secret by lazy { loadSecretFile("secret.json") }

    @Before
    fun setup() {
        val response = client.login(
            LoginParams(
                secret.getString("email"),
                secret.getString("password")
            )
        ).execute()
        response.isSuccessful shouldBe true
    }

    @Test
    fun `list transaction should be returned successfully`() {
        val response = client.getTransactions(
            TransactionListParams.create(
                searchTerm = null
            )
        ).execute()

        response.isSuccessful shouldBe true
        response.body()?.data?.pagination?.perPage shouldBe 10
        response.body()?.data?.pagination?.currentPage shouldBe 1
        response.body()?.data?.data?.size shouldBe 10
    }

    @Test
    fun `list transaction with a specific account should return transactions associated with the account`() {
        val response = client.getTransactions(
            TransactionListParams.create(
                searchTerm = secret.getString("account_address")
            )
        ).execute()

        response.isSuccessful shouldBe true
        response.body()?.data?.data?.forEach {
            (secret.getString("account_address") in arrayOf(it.from.address, it.to.address)) shouldBe true
        }
    }
}
