package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 22/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.support.test.runner.AndroidJUnit4
import co.omisego.omisego.extension.bd
import co.omisego.omisego.utils.validateParcel
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.Date

@RunWith(AndroidJUnit4::class)
@Config(sdk = [21])
class SettingTest {
    private lateinit var setting: Setting

    @Before
    fun setup() {
        setting = Setting(listOf(
            Token("1", "OMG", "OmiseGO", 10000.bd, Date(), Date(), mapOf(), mapOf()),
            Token("2", "ETH", "Ether", 10000000000000.bd, Date(), Date(), mapOf(), mapOf())
        ))
    }

    @Test
    fun `Setting should be parcelized correctly`() {
        setting.validateParcel().apply {
            this shouldEqual setting
            this shouldNotBe setting
        }
    }
}
