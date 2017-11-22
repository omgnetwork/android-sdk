package co.omisego.androidsdk.models


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
data class Balance(val amount: Double, val mintedToken: MintedToken)