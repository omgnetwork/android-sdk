package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/14/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * Represent a wallet containing a list of balances.
 *
 * @param address The address of the balances.
 * @param balances The list of balances associated with that address.
 * @param name The name of the wallet.
 * @param identifier The identifier of the wallet.
 * @param userId The id of the user associated to this wallet if it's a user wallet. Null if it's an account wallet.
 * @param user The user associated to this wallet if it's a user wallet. Null if it's an account wallet.
 * @param accountId The id of the account associated to this wallet if it's an account wallet. Null if it's a user wallet.
 * @param account The account associated to this wallet if it's an account wallet. Null if it's a user wallet.
 * @param metadata Any additional metadata that need to be stored as a dictionary
 * @param encryptedMetadata Any additional encrypted metadata that need to be stored as a dictionary
 */
data class Wallet(
    val address: String,
    val balances: List<Balance>,
    val name: String,
    val identifier: String,
    val userId: String?,
    val user: User?,
    val accountId: String?,
    val account: Account?,
    val metadata: Map<String, Any>,
    val encryptedMetadata: Map<String, Any>
)
