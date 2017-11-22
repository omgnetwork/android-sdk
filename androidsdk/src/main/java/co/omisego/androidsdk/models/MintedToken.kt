package co.omisego.androidsdk.models


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/3/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

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
data class MintedToken(val id: String, val symbol: String, val name: String, val subUnitToUnit: Double)
