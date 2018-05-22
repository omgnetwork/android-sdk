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
class AddressTest {
    private lateinit var address: Address

    @Before
    fun setup() {
        address = Address("1234-1234-1234", listOf(
            Balance(100.bd, MintedToken("1234", "OMG", "OmiseGO", 100.bd)),
            Balance(100000000.bd, MintedToken("1234-1234-1235-12345", "ETH", "Ether", 100000000.bd))
        ))
    }

    @Test
    fun `Address should be parcelized correctly`() {
        address.validateParcel().apply {
            this shouldEqual this@AddressTest.address
            this shouldNotBe this@AddressTest.address
        }
    }
}