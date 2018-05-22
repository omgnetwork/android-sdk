package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/6/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Represents a balance of a token
 *
 * @param token The token corresponding to the balance
 * @param amount The total amount of token available for the current user.
 */
@Parcelize
data class Balance(val amount: BigDecimal, val token: Token): Parcelable {

    /**
     * Helper method that returns an easily readable value of the amount
     *
     * @param precision The decimal precision to give to the formatter
     * for example, a number 0.123 with a precision 2 will be 0.12
     * @return The formatted balance amount with thousand separator
     */
    fun displayAmount(precision: Int = 2): String {
        return "%,.${precision}f".format(amount.divide(token.subunitToUnit, precision, RoundingMode.FLOOR))
    }

    /**
     * Returns a [Balance] whose amount is (this.amount + augend.amount)
     *
     * @param augend balance to be added to this Balance
     * @return An added Balance whose amount is this.amount + augend.amount
     * @throws UnsupportedOperationException if [Token]'s compatWith is return false
     */
    operator fun plus(augend: Balance): Balance {
        if (this.token compatWith augend.token) {
            val newAmount = this.amount.plus(augend.amount)
            return Balance(newAmount, this.token.copy())
        }
        throw UnsupportedOperationException("Balances are not compatible. Make sure Tokens have the same symbol and subunitToUnit.")
    }

    /**
     * Returns a [Balance] whose amount is (this.amount - subtrahend.amount)
     *
     * @param subtrahend balance to be subracted to this Balance
     * @return An added Balance whose amount is this.amount - subtrahend.amount
     * @throws UnsupportedOperationException if [Token]'s compatWith is return false
     */
    operator fun minus(subtrahend: Balance): Balance {
        if (this.token compatWith subtrahend.token) {
            val newAmount = this.amount.subtract(subtrahend.amount)
            return Balance(newAmount, this.token.copy())
        }
        throw UnsupportedOperationException("Balances are not compatible. Make sure Tokens have the same symbol and subunitToUnit.")
    }
}