package co.omisego.omisego.live.auth

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.extension.bd
import co.omisego.omisego.live.BaseAuthTest
import co.omisego.omisego.model.params.TransactionCreateParams
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class TransferLiveTest : BaseAuthTest() {

    @Test
    fun `transfer from an account to a user should return 200, 'from' should be an account and 'to' should be a user`() {
        val response = client.createTransaction(
            TransactionCreateParams(
                fromAddress = secret.getString("account_address"),
                toAddress = secret.getString("user_address"),
                tokenId = secret.getString("token_id"),
                amount = 1.bd
            )
        ).execute()

        response.isSuccessful shouldBe true

        /* Transaction source 'from' should be an account */
        response.body()?.data?.from?.accountId shouldNotBe null
        response.body()?.data?.from?.userId shouldBe null

        /* Transaction source 'to' should be a user */
        response.body()?.data?.to?.accountId shouldBe null
        response.body()?.data?.to?.userId shouldNotBe null
    }

    @Test
    fun `transfer from a user to an account should return 200, 'from' should be a user and 'to' should be an account`() {
        val response = client.createTransaction(
            TransactionCreateParams(
                fromAddress = secret.getString("user_address"),
                toAddress = secret.getString("account_address"),
                tokenId = secret.getString("token_id"),
                amount = 1.bd
            )
        ).execute()

        response.isSuccessful shouldBe true

        /* Transaction source 'from' should be a user */
        response.body()?.data?.from?.accountId shouldBe null
        response.body()?.data?.from?.userId shouldNotBe null

        /* Transaction source 'to' should be an account */
        response.body()?.data?.to?.accountId shouldNotBe null
        response.body()?.data?.to?.userId shouldBe null
    }
}
