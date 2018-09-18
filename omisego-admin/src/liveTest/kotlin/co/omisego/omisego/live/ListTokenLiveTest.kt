package co.omisego.omisego.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.LiveTest
import co.omisego.omisego.model.params.LoginParams
import co.omisego.omisego.model.params.TokenListParams
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeGreaterThan
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class ListTokenLiveTest : LiveTest() {
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
    fun `list tokens should be returned successfully`() {
        val response = client.getTokens(
            TokenListParams.create(
                searchTerm = null
            )
        ).execute()

        response.isSuccessful shouldBe true
        response.body()?.data?.pagination?.perPage shouldBe 10
        response.body()?.data?.pagination?.currentPage shouldBe 1
        response.body()?.data?.data?.size!! shouldBeGreaterThan 0
    }
}
