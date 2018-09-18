package co.omisego.omisego.live.unauth

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.live.BaseLiveTest
import co.omisego.omisego.model.ClientAuthenticationToken
import co.omisego.omisego.model.params.LoginParams
import co.omisego.omisego.model.params.SignUpParams
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class AuthenticationLiveTest : BaseLiveTest() {
    @Test
    fun `login should return 200 and contain ClientAuthenticationToken object`() {
        val response = client.login(
            LoginParams(
                secret.getString("email"),
                secret.getString("password")
            )
        ).execute()
        response.isSuccessful shouldBe true
        response.body()?.data shouldBeInstanceOf ClientAuthenticationToken::class
    }

    @Test
    fun `signup should return 200`() {
        val response = client.signup(
            SignUpParams(
                "android_test_${UUID.randomUUID().toString().takeLast(5)}@test.co",
                "password",
                "password",
                "${secret.getString("base_url")}/success"
            )
        ).execute()
        response.isSuccessful shouldBe true
    }
}
