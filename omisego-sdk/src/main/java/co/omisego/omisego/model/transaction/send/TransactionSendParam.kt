package co.omisego.omisego.model.transaction.send

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 7/6/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * Represents a structure used to create a transaction
 *
 * @param from The address from which to take the tokens (which must belong to the user).
 * If not specified, the user's primary balance will be used.
 * @param to The address where to send the tokens.
 * @param amount The amount of minted token to transfer (down to subunit to unit).
 * @param tokenId The id of the minted token to send.
 * @param metadata Additional metadata for the transaction.
 * @param encryptedMetadata Additional encrypted metadata for the transaction.
 */
data class TransactionSendParam(
    val from: String?,
    val to: String,
    val amount: Double,
    val tokenId: String,
    val metadata: Map<String, Any>,
    val encryptedMetadata: Map<String, Any>
)
