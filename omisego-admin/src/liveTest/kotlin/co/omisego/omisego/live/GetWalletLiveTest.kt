package co.omisego.omisego.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.LiveTest
import co.omisego.omisego.model.params.LoginParams
import co.omisego.omisego.model.params.WalletParams
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class GetWalletLiveTest : LiveTest() {
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
