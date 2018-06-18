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

@RunWith(AndroidJUnit4::class)
@Config(sdk = [23])
class WalletTest {
    private lateinit var address: Wallet

    @Before
    fun setup() {
        address = Wallet(
            "1234-1234-1234",
            listOf(
                Balance(100.bd, Token("1234", "OMG", "OmiseGO", 100.bd, mapOf(), mapOf())),
                Balance(100000000.bd, Token("1234-1234-1235-12345", "ETH", "Ether", 100000000.bd, mapOf(), mapOf()))
            ),
            "",
            "",
            null,
            null,
            null,
            null,
            mapOf(),
            mapOf()
        )
    }

    @Test
    fun `Wallet should be parcelized correctly`() {
        address.validateParcel().apply {
            this shouldEqual this@WalletTest.address
            this shouldNotBe this@WalletTest.address
        }
    }
}