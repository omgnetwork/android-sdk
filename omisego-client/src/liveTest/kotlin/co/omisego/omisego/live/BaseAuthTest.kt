package co.omisego.omisego.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.ClientAuthenticationToken
import co.omisego.omisego.model.params.LoginParams
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
open class BaseAuthTest : BaseLiveTest() {

    private lateinit var clientAuthenticationToken: ClientAuthenticationToken

    @Before
    open fun setup() {
        val response = client.login(
            LoginParams(
                secret.getString("email"),
                secret.getString("password")
            )
        ).execute()
        response.isSuccessful shouldBe true
        clientAuthenticationToken = response.body()!!.data
    }

    @Test
    fun `should be setup authentication correctly`() {
        clientAuthenticationToken.authenticationToken.isEmpty() shouldBe false
        clientAuthenticationToken.userId.isEmpty() shouldBe false
        clientAuthenticationToken.user shouldNotBe null
    }
}
