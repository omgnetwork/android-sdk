package co.omisego.omisego.live.auth

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.BaseAuthTest
import co.omisego.omisego.model.params.WalletParams
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class GetWalletLiveTest : BaseAuthTest() {

    @Test
    fun `get a user wallet should be returned associated user of the account`() {
        val response = client.getWallet(
            WalletParams(secret.getString("user_address"))
        ).execute()

        response.isSuccessful shouldBe true
        response.body()?.data?.user shouldNotBe null
        response.body()?.data?.account shouldBe null
    }

    @Test
    fun `get an account wallet should be returned associated account of the account`() {
        val response = client.getWallet(
            WalletParams(secret.getString("account_address"))
        ).execute()

        response.isSuccessful shouldBe true
        response.body()?.data?.account shouldNotBe null
        response.body()?.data?.user shouldBe null
    }
}
