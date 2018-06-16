package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/3/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.math.BigDecimal

/**
 * Represents a token
 *
 * @param id The id of the token
 * @param symbol The symbol of the token
 * @param name The full name of the token
 * @param subunitToUnit The multiplier representing the value of 1 token,
 *  i.e: If I want to give or receive 13 tokens and the [subunitToUnit] is 1000,
 *  then the amount will be 13*1000 = 13000
 * @param metadata Any additional metadata that need to be stored as a [HashMap].
 * @param encryptedMetadata Any additional encrypted metadata that need to be stored as a dictionary.
 */
@Parcelize
data class Token(
    val id: String,
    val symbol: String,
    val name: String,
    val subunitToUnit: BigDecimal,
    val metadata: @RawValue Map<String, Any>,
    val encryptedMetadata: @RawValue Map<String, Any>
) : Parcelable {

    /**
     * Compares the current [Token] with the specified [Token] for verifying both [Token] are compatible.
     * Returns a boolean, true, or false as the [symbol] and the [subunitToUnit] are equal to, or are not equal to the specified [Token].
     *
     * @param token the token to be compared.
     * @return a boolean, true, or false as the [symbol] and the [subunitToUnit] are equal to, or are not equal to the specified [Token].
     */
    infix fun compatWith(token: Token) = this.symbol == token.symbol && this.subunitToUnit == token.subunitToUnit
}
