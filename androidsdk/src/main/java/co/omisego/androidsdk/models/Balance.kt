package co.omisego.androidsdk.models

import java.math.BigDecimal
import java.math.RoundingMode


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/6/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

/**
 * Represents a balance of a minted token
 *
 * @param mintedToken The minted token corresponding to the balance
 * @param amount The total amount of minted token available for the current user.
 */
data class Balance(val amount: BigDecimal, val mintedToken: MintedToken) {

    /**
     * Helper method that returns an easily readable value of the amount
     *
     * @param precision The decimal precision to give to the formatter
     * for example, a number 0.123 with a precision 2 will be 0.12
     * @return The formatted balance amount with thousand separator
     */
    fun displayAmount(precision: Int = 2): String {
        return "%,.${precision}f".format(amount.divide(mintedToken.subUnitToUnit, precision, RoundingMode.FLOOR))
    }
}