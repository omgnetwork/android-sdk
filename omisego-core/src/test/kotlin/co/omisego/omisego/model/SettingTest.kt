package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 22/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.support.test.runner.AndroidJUnit4
import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.utils.validateParcel
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [21])
class SettingTest : GsonDelegator() {
    private val settingFile by ResourceFile("setting.json", "object")
    private val setting: Setting by lazy { gson.fromJson(settingFile.readText(), Setting::class.java) }

    @Test
    fun `setting should be parcelized correctly`() {
        setting.validateParcel().apply {
            this shouldEqual setting
            this shouldNotBe setting
        }
    }

    @Test
    fun `setting should be parsed correctly`() {
        with(setting) {
            tokens shouldBeInstanceOf List::class.java
            tokens[0] shouldBeInstanceOf Token::class.java
        }
    }
}
