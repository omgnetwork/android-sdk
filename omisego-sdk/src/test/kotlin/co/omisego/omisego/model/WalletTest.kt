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
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [23])
class WalletTest : GsonDelegator() {
    private val walletFile by ResourceFile("wallet.json", "object")
    private val wallet by lazy { gson.fromJson(walletFile.readText(), Wallet::class.java) }

    @Test
    fun `wallet should be parcelized correctly`() {
        wallet.validateParcel().apply {
            this shouldEqual wallet
            this shouldNotBe wallet
        }
    }

    @Test
    fun `wallet should be parsed correctly`() {
        with(wallet) {
            address shouldEqual "2c2e0f2e-fa0f-4abe-8516-9e92cf003486"
            name shouldEqual "primary"
            identifier shouldEqual "primary"
            userId shouldEqual "cec34607-0761-4a59-8357-18963e42a1aa"
            accountId shouldEqual null
            account shouldEqual null
            user shouldBeInstanceOf User::class.java
            balances shouldBeInstanceOf List::class.java
            balances.size shouldEqualTo 1
            balances[0] shouldBeInstanceOf Balance::class.java
        }
    }
}
