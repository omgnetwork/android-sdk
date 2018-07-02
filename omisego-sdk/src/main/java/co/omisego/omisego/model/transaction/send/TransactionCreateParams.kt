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
 * @param fromAddress The address from which to take the tokens (which must belong to the user).
 * If not specified, the user's primary address will be used.
 * @param toAddress The address where to send the tokens.
 * @param toProviderUserId The provider user id where to send the token.
 * @param toAccountId The account id where to send the tokens.
 * @param amount The amount of token to transfer (down to subunit to unit).
 * @param tokenId The id of the token to send.
 * @param idempotencyToken The idempotency token to use for send the transaction.
 * @param metadata Additional metadata for the transaction.
 * @param encryptedMetadata Additional encrypted metadata for the transaction.
 */
data class TransactionCreateParams(
    val fromAddress: String?,
    val toAddress: String,
    val toProviderUserId: String?,
    val toAccountId: String?,
    val amount: Double,
    val tokenId: String,
    val idempotencyToken: String = "$toAddress-${System.nanoTime()}",
    val metadata: Map<String, Any>,
    val encryptedMetadata: Map<String, Any>
)
