package co.omisego.omisego.model.params.admin

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 25/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.TransactionRequest
import java.math.BigDecimal

/**
 * Represents a structure used to consume a transaction request
 *
 * @param formattedTransactionRequestId The formattedTransactionRequestId of the transaction request to be consumed
 * @param amount The amount of token to createTransaction (down to subunit to unit)
 * @param address The address to use for the consumption
 * @param idempotencyToken The idempotency token to use for the consumption
 * @param correlationId An id that can uniquely identify a transaction. Typically an order id from a provider.
 * @param metadata Additional metadata for the consumption
 * @param encryptedMetadata Additional encrypted metadata for the consumption
 * @param accountId The account id consuming the request
 * @param userId The user id of the user for whom the request is consumed
 * @param providerUserId The provider user id of the user for whom the request is consumed
 * @param tokenId The id of the token to use for the consumption.
 * If different from the request, the exchange account or wallet address must be specified
 * @param exchangeAccountId The account to use for the token exchange (if any)
 * @param exchangeWalletAddress The wallet address to use for the token exchange (if any)
 */
data class TransactionConsumptionParams internal constructor(
    private val formattedTransactionRequestId: String,
    var amount: BigDecimal? = null,
    val address: String? = null,
    val idempotencyToken: String = "$formattedTransactionRequestId-${System.nanoTime()}",
    val correlationId: String? = null,
    val metadata: Map<String, Any> = mapOf(),
    val encryptedMetadata: Map<String, Any> = mapOf(),
    val accountId: String? = null,
    val userId: String? = null,
    val providerUserId: String? = null,
    val tokenId: String? = null,
    val exchangeAccountId: String? = null,
    val exchangeWalletAddress: String? = null
) {
    companion object {

        /**
         * Initialize the params used to consume a transaction request
         * Returns null if the amount is null and was not specified in the transaction request
         *
         * @param transactionRequest The transaction request to be consumed
         * @param amount The amount of token to createTransaction (down to subunit to unit)
         * @param address The address to use for the consumption
         * @param idempotencyToken The idempotency token to use for the consumption
         * @param correlationId An id that can uniquely identify a transaction. Typically an order id from a provider.
         * @param accountId The account id consuming the request
         * @param userId The user id of the user for whom the request is consumed
         * @param providerUserId The provider user id of the user for whom the request is consumed
         * @param tokenId The id of the token to use for the consumption.
         * If different from the request, the exchange account or wallet address must be specified
         * @param exchangeAccountId The account to use for the token exchange (if any)
         * @param exchangeWalletAddress The wallet address to use for the token exchange (if any)
         * @param metadata Additional metadata for the consumption
         * @param encryptedMetadata Additional encrypted metadata for the consumption
         */
        fun create(
            transactionRequest: TransactionRequest,
            amount: BigDecimal? = null,
            address: String? = null,
            idempotencyToken: String = "${transactionRequest.id}-${System.nanoTime()}",
            correlationId: String? = null,
            accountId: String? = null,
            userId: String? = null,
            providerUserId: String? = null,
            tokenId: String? = null,
            exchangeAccountId: String? = null,
            exchangeWalletAddress: String? = null,
            metadata: Map<String, Any> = mapOf(),
            encryptedMetadata: Map<String, Any> = mapOf()
        ): TransactionConsumptionParams {
            require(transactionRequest.amount != null || amount != null) {
                "The transactionRequest amount or the amount of token to createTransaction should be provided"
            }

            return TransactionConsumptionParams(
                transactionRequest.formattedId,
                if (transactionRequest.amount?.stripTrailingZeros() == amount?.stripTrailingZeros()) null else amount,
                address,
                idempotencyToken,
                correlationId,
                metadata,
                encryptedMetadata,
                accountId,
                userId,
                providerUserId,
                tokenId,
                exchangeAccountId,
                exchangeWalletAddress
            )
        }


        /**
         * Initialize the params used to consume a transaction request
         * Returns null if the amount is null and was not specified in the transaction request
         *
         * @param formattedTransactionRequestId The formatted id of the transaction request to consume
         * @param amount The amount of token to createTransaction (down to subunit to unit)
         * @param address The address to use for the consumption
         * @param idempotencyToken The idempotency token to use for the consumption
         * @param correlationId An id that can uniquely identify a transaction. Typically an order id from a provider.
         * @param accountId The account id consuming the request
         * @param userId The user id of the user for whom the request is consumed
         * @param providerUserId The provider user id of the user for whom the request is consumed
         * @param tokenId The id of the token to use for the consumption.
         * If different from the request, the exchange account or wallet address must be specified
         * @param exchangeAccountId The account to use for the token exchange (if any)
         * @param exchangeWalletAddress The wallet address to use for the token exchange (if any)
         * @param metadata Additional metadata for the consumption
         * @param encryptedMetadata Additional encrypted metadata for the consumption
         */
        fun create(
            formattedTransactionRequestId: String,
            amount: BigDecimal? = null,
            address: String? = null,
            idempotencyToken: String = "$formattedTransactionRequestId-${System.nanoTime()}",
            correlationId: String? = null,
            accountId: String? = null,
            userId: String? = null,
            providerUserId: String? = null,
            tokenId: String? = null,
            exchangeAccountId: String? = null,
            exchangeWalletAddress: String? = null,
            metadata: Map<String, Any> = mapOf(),
            encryptedMetadata: Map<String, Any> = mapOf()
        ): TransactionConsumptionParams {

            return TransactionConsumptionParams(
                formattedTransactionRequestId,
                amount,
                address,
                idempotencyToken,
                correlationId,
                metadata,
                encryptedMetadata,
                accountId,
                userId,
                providerUserId,
                tokenId,
                exchangeAccountId,
                exchangeWalletAddress
            )
        }
    }
}
