package co.omisego.omisego.model.params.admin

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
 * @param fromProviderUserId The provider user id where to take the token from
 * @param toProviderUserId The provider user id where to send the token.
 * @param fromAccountId The account id where to take the token from
 * @param toAccountId The account id where to send the tokens.
 * @param fromUserId The user id where to take the token from
 * @param toUserId The user id where to send the token
 * @param fromAmount The amount of token to send (down to subunit to unit)
 * @param toAmount The amount of token expected to be received (down to subunit to unit)
 * @param fromTokenId The id of the token that will be used to send the funds
 * @param toTokenId The id of the token that will be used to receive the funds
 * @param amount The amount of token to transfer (down to subunit to unit).
 * @param tokenId The id of the token to send.
 * @param exchangeAccountId The account id to use for exchanging the tokens
 * @param exchangeAddress The address to use for exchanging the tokens
 * @param idempotencyToken The idempotency token to use for send the transaction.
 * @param metadata Additional metadata for the transaction.
 * @param encryptedMetadata Additional encrypted metadata for the transaction.
 */
data class TransactionCreateParams(
    val fromAddress: String? = null,
    val toAddress: String,
    val fromProviderUserId: String? = null,
    val toProviderUserId: String? = null,
    val fromAccountId: String? = null,
    val toAccountId: String? = null,
    val fromUserId: String? = null,
    val toUserId: String? = null,
    val fromAmount: BigDecimal? = null,
    val toAmount: BigDecimal? = null,
    val fromTokenId: String? = null,
    val toTokenId: String? = null,
    val amount: BigDecimal,
    val tokenId: String,
    val exchangeAccountId: String? = null,
    val exchangeAddress: String? = null,
    val idempotencyToken: String = "$toAddress-${System.nanoTime()}",
    val metadata: Map<String, Any> = mapOf(),
    val encryptedMetadata: Map<String, Any> = mapOf()
)
