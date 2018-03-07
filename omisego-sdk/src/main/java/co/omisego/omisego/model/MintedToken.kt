package co.omisego.omisego.model

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/3/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * Represents a minted token
 *
 * @param id The id of the minted token
 * @param symbol The symbol of the minted token
 * @param name The full name of the minted token
 * @param subUnitToUnit The multiplier representing the value of 1 minted token,
 *  i.e: If I want to give or receive 13 minted tokens and the [subUnitToUnit] is 1000,
 *  then the amount will be 13*1000 = 13000
 */
data class MintedToken(val id: String, val symbol: String, val name: String, @SerializedName("subunit_to_unit") val subUnitToUnit: BigDecimal) {

    /**
     * Compares the current [MintedToken] with the specified [MintedToken] for verifying both [MintedToken] are compatible.
     * Returns a boolean, true, or false as the [symbol] and the [subUnitToUnit] are equal to, or are not equal to the specified [MintedToken].
     *
     * @param mintedToken the mintedToken to be compared.
     * @return a boolean, true, or false as the [symbol] and the [subUnitToUnit] are equal to, or are not equal to the specified [MintedToken].
     */
    infix fun compatWith(mintedToken: MintedToken) = this.symbol == mintedToken.symbol && this.subUnitToUnit == mintedToken.subUnitToUnit
}
