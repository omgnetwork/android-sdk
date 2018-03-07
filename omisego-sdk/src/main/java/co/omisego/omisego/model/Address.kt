package co.omisego.omisego.model

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/14/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * Represent an address containing a list of balances
 *
 * @param address The address of the balances
 * @param balances The list of balances associated with that address
 */
data class Address(val address: String, val balances: List<Balance>)