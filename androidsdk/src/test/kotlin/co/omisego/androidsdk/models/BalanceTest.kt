package co.omisego.androidsdk.models

import co.omisego.androidsdk.extensions.bd
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 6/12/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class BalanceTest {

    private var amount: BigDecimal = 0.0.bd
    private var subUnitToUnit: BigDecimal = 100_000.0.bd
    private lateinit var mintedToken: MintedToken

    @Before
    fun setup() {
        mintedToken = MintedToken("omg:1234", "OMG", "OmiseGO", subUnitToUnit)
    }

    @Test
    fun `test very big amount and small subUnitToUnit`() {
        // 36 zeros
        amount = 1_000_000_000_000_000_000_000_000_000_000_000_000.0.bd
        val token = mintedToken.copy(subUnitToUnit = 1.0.bd)
        val balance = Balance(amount, token)
        balance.displayAmount() shouldEqual "1,000,000,000,000,000,000,000,000,000,000,000,000.00"
    }

    @Test
    fun `test small amount and very big subUnitToUnit `() {
        amount = 1.0.bd
        val token = mintedToken.copy(subUnitToUnit = 1_000_000_000_000_000_000_000_000_000_000_000_000.0.bd)
        val balance = Balance(amount, token)

        // 36 decimal
        balance.displayAmount(36) shouldEqual "0.000000000000000000000000000000000001"
    }

    @Test
    fun `test zero display`() {
        amount = 0.0.bd
        val token = mintedToken.copy(subUnitToUnit = 1.0.bd)
        val balance = Balance(amount, token)

        balance.displayAmount(0) shouldEqual "0"
    }

    @Test
    fun `test zero display with 5 precision`() {
        amount = 0.0.bd
        val token = mintedToken.copy(subUnitToUnit = 1.0.bd)
        val balance = Balance(amount, token)

        balance.displayAmount(5) shouldEqual "0.00000"
    }

    @Test
    fun `test division with cool divisor number and display very large precision correctly`() {
        val coolDivisorNumber = 998_001.0.bd
        val token = mintedToken.copy(subUnitToUnit = coolDivisorNumber)
        val balance = Balance(1.0.bd, token)

        balance.displayAmount(99) shouldEqual "0.000001002003004005006007008009010011012013014015016017018019020021022023024025026027028029030031032"
    }
}


