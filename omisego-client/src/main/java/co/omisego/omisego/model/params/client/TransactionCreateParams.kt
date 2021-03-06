package co.omisego.omisego.model.params.client

import java.math.BigDecimal

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
data class TransactionCreateParams private constructor(
    val fromAddress: String? = null,
    val toAddress: String? = null,
    val toProviderUserId: String? = null,
    val toAccountId: String? = null,
    val amount: BigDecimal,
    val tokenId: String,
    val idempotencyToken: String = "$toAddress-${System.nanoTime()}",
    val metadata: Map<String, Any> = mapOf(),
    val encryptedMetadata: Map<String, Any> = mapOf()
) {
    constructor(
        fromAddress: String? = null,
        toAddress: String,
        amount: BigDecimal,
        tokenId: String,
        idempotencyToken: String = "$toAddress-${System.nanoTime()}",
        metadata: Map<String, Any> = mapOf(),
        encryptedMetadata: Map<String, Any> = mapOf()
    ) : this(
        fromAddress,
        toAddress,
        null,
        null,
        amount,
        tokenId,
        idempotencyToken,
        metadata,
        encryptedMetadata
    )

    constructor(
        fromAddress: String? = null,
        toAddress: String? = null,
        toAccountId: String,
        amount: BigDecimal,
        tokenId: String,
        idempotencyToken: String = "$toAddress-${System.nanoTime()}",
        metadata: Map<String, Any> = mapOf(),
        encryptedMetadata: Map<String, Any> = mapOf()
    ) : this(
        fromAddress,
        toAddress,
        null,
        toAccountId,
        amount,
        tokenId,
        idempotencyToken,
        metadata,
        encryptedMetadata
    )

    constructor(
        fromAddress: String? = null,
        toAddress: String? = null,
        amount: BigDecimal,
        toProviderUserId: String,
        tokenId: String,
        idempotencyToken: String = "$toAddress-${System.nanoTime()}",
        metadata: Map<String, Any> = mapOf(),
        encryptedMetadata: Map<String, Any> = mapOf()
    ) : this(
        fromAddress,
        toAddress,
        toProviderUserId,
        null,
        amount,
        tokenId,
        idempotencyToken,
        metadata,
        encryptedMetadata
    )
}
