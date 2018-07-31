package co.omisego.omisego.model

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 6/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.support.test.runner.AndroidJUnit4
import co.omisego.omisego.extension.bd
import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.client.utils.validateParcel
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.math.BigDecimal
import java.util.Date

@RunWith(AndroidJUnit4::class)
@Config(sdk = [23])
class BalanceTest : GsonDelegator() {

    private val balanceFile by ResourceFile("balance.json", "object")
    private val balance: Balance by lazy { gson.fromJson(balanceFile.readText(), Balance::class.java) }
    private var amount: BigDecimal = 0.0.bd
    private var subUnitToUnit: BigDecimal = 100_000.0.bd
    private lateinit var token: Token

    @Before
    fun setup() {
        token = Token("omg:1234", "OMG", "OmiseGO", subUnitToUnit, Date(), Date(), mapOf(), mapOf())
    }

    @Test
    fun `test very big amount and small subUnitToUnit`() {
        // 36 zeros
        amount = 1_000_000_000_000_000_000_000_000_000_000_000_000.0.bd
        val token = token.copy(subunitToUnit = 1.0.bd)
        val balance = Balance(amount, token)
        balance.displayAmount() shouldEqual "1,000,000,000,000,000,000,000,000,000,000,000,000.00"
    }

    @Test
    fun `test small amount and very big subUnitToUnit `() {
        amount = 1.0.bd
        val token = token.copy(subunitToUnit = 1_000_000_000_000_000_000_000_000_000_000_000_000.0.bd)
        val balance = Balance(amount, token)

        // 36 decimal
        balance.displayAmount(36) shouldEqual "0.000000000000000000000000000000000001"
    }

    @Test
    fun `test zero display`() {
        amount = 0.0.bd
        val token = token.copy(subunitToUnit = 1.0.bd)
        val balance = Balance(amount, token)

        balance.displayAmount(0) shouldEqual "0"
    }

    @Test
    fun `test zero display with 5 precision`() {
        amount = 0.0.bd
        val token = token.copy(subunitToUnit = 1.0.bd)
        val balance = Balance(amount, token)

        balance.displayAmount(5) shouldEqual "0.00000"
    }

    @Test
    fun `test division with cool divisor number and display very large precision correctly`() {
        val coolDivisorNumber = 998_001.0.bd
        val token = token.copy(subunitToUnit = coolDivisorNumber)
        val balance = Balance(1.0.bd, token)

        balance.displayAmount(99) shouldEqual "0.000001002003004005006007008009010011012013014015016017018019020021022023024025026027028029030031032"
    }

    @Test
    fun `test balance + another balance should be correct`() {
        val balance1 = Balance(1_999_000_000_000.0.bd, Token("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10000.0.bd, Date(), Date(), mapOf(), mapOf()))
        val balance2 = Balance(9_999_000_000_000.0.bd, Token("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10000.0.bd, Date(), Date(), mapOf(), mapOf()))

        val result = balance1 + balance2
        result.amount shouldEqual 11_998_000_000_000.0.bd
    }

    @Test
    fun `test balance - another balance should be correct`() {
        val balance1 = Balance(11_998_000_000_000.0.bd, Token("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10000.0.bd, Date(), Date(), mapOf(), mapOf()))
        val balance2 = Balance(9_999_000_000_000.0.bd, Token("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10000.0.bd, Date(), Date(), mapOf(), mapOf()))

        val result = balance1 - balance2
        result.amount shouldEqual 1_999_000_000_000.0.bd

        val result2 = balance2 - balance1
        result2.amount shouldEqual (-1_999_000_000_000.0).bd
    }

    @Test(expected = UnsupportedOperationException::class)
    fun `test balance + incompatible balance should throw UnSupportOperationException`() {
        val balance1 = Balance(1_999_000_000_000.0.bd, Token("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10000.0.bd, Date(), Date(), mapOf(), mapOf()))
        val balance2 = Balance(9_999_000_000_000.0.bd, Token("ETH:8bcda572-9411-43c8-baae-cd56eb0155f3", "ETH", "Etherium", 10000.0.bd, Date(), Date(), mapOf(), mapOf()))

        balance1 + balance2
    }

    @Test
    fun `balance should be parcelized correctly`() {
        val balance1 = Balance(1_999_000_000_000.0.bd, Token("OMG:8bcda572-9411-43c8-baae-cd56eb0155f3", "OMG", "OmiseGO", 10000.0.bd, Date(), Date(), mapOf(), mapOf()))
        balance1.validateParcel().apply {
            this shouldEqual balance1
            this shouldNotBe balance1
        }
    }

    @Test
    fun `balance should be parsed correctly`() {
        with(balance) {
            amount shouldEqual 103_100.bd
            token shouldNotBe null
            token shouldBeInstanceOf Token::class.java
        }
    }
}
