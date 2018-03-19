package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/6/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import java.math.BigDecimal
import java.math.RoundingMode

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
        return "%,.${precision}f".format(amount.divide(mintedToken.subunitToUnit, precision, RoundingMode.FLOOR))
    }

    /**
     * Returns a [Balance] whose amount is (this.amount + augend.amount)
     *
     * @param augend balance to be added to this Balance
     * @return An added Balance whose amount is this.amount + augend.amount
     * @throws UnsupportedOperationException if [MintedToken]'s compatWith is return false
     */
    operator fun plus(augend: Balance): Balance {
        if (this.mintedToken compatWith augend.mintedToken) {
            val newAmount = this.amount.plus(augend.amount)
            return Balance(newAmount, this.mintedToken.copy())
        }
        throw UnsupportedOperationException("Balances are not compatible. Make sure MintedTokens have the same symbol and subunitToUnit.")
    }

    /**
     * Returns a [Balance] whose amount is (this.amount - subtrahend.amount)
     *
     * @param subtrahend balance to be subracted to this Balance
     * @return An added Balance whose amount is this.amount - subtrahend.amount
     * @throws UnsupportedOperationException if [MintedToken]'s compatWith is return false
     */
    operator fun minus(subtrahend: Balance): Balance {
        if (this.mintedToken compatWith subtrahend.mintedToken) {
            val newAmount = this.amount.subtract(subtrahend.amount)
            return Balance(newAmount, this.mintedToken.copy())
        }
        throw UnsupportedOperationException("Balances are not compatible. Make sure MintedTokens have the same symbol and subunitToUnit.")
    }
}