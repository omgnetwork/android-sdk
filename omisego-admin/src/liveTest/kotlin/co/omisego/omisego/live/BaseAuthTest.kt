package co.omisego.omisego.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 18/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.AdminAuthenticationToken
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

    private lateinit var adminAuthenticationToken: AdminAuthenticationToken

    @Before
    open fun setup() {
        val response = client.login(
            LoginParams(
                secret.getString("email"),
                secret.getString("password")
            )
        ).execute()
        response.isSuccessful shouldBe true
        adminAuthenticationToken = response.body()!!.data
    }

    @Test
    fun `should be setup authentication correctly`() {
        adminAuthenticationToken.authenticationToken.isEmpty() shouldBe false
        adminAuthenticationToken.accountId.isEmpty() shouldBe false
        adminAuthenticationToken.account shouldNotBe null
    }
}
