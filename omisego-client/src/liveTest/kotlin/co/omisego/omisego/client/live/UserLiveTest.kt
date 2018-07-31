package co.omisego.omisego.client.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.User
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class UserLiveTest : LiveTest() {

    @Test
    fun `get_current_user should return 200 and parsed the response correctly`() {
        val user = client.getCurrentUser().execute()
        user.isSuccessful shouldBe true
        user.body()?.data shouldNotBe null
        user.body()?.data shouldBeInstanceOf User::class.java
    }
}
