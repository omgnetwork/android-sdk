package co.omisego.omisego.client.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.Wallet
import co.omisego.omisego.model.WalletList
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class WalletLiveTest : LiveTest() {

    @Test
    fun `get_wallets should return 200 and parsed the response correctly`() {
        val walletList = client.getWallets().execute()
        walletList.isSuccessful shouldBe true
        walletList.body()?.data shouldNotBe null
        walletList.body()?.data shouldBeInstanceOf WalletList::class.java
        walletList.body()?.data?.data?.size!! shouldBeGreaterThan 0
        walletList.body()?.data?.data?.get(0) shouldBeInstanceOf Wallet::class.java
    }
}
