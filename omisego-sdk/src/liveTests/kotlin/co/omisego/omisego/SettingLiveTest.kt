package co.omisego.omisego

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.Setting
import co.omisego.omisego.model.Token
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBe
import org.junit.Test

class SettingLiveTest : LiveTest() {

    @Test
    fun `get_settings should return 200 and parsed the response correctly`() {
        val setting = client.getSettings().execute()
        setting.isSuccessful shouldBe true
        setting.body()?.data shouldNotBe null
        setting.body()?.data shouldBeInstanceOf Setting::class.java
        setting.body()?.data?.tokens?.size!! shouldBeGreaterThan 0
        setting.body()?.data?.tokens?.get(0) shouldBeInstanceOf Token::class.java
    }
}
