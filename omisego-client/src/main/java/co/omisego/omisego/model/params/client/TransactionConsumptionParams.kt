package co.omisego.omisego.model.params.client

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
 */
data class TransactionConsumptionParams internal constructor(
    /**
     * The formattedTransactionRequestId of the transaction request to be consumed
     */
    private val formattedTransactionRequestId: String,

    /**
     * The amount of token to transfer (down to subunit to unit)
     */
    var amount: BigDecimal? = null,

    /**
     * The address to use for the consumption
     */
    val address: String? = null,

    /**
     * The idempotency token to use for the consumption
     */
    val idempotencyToken: String = "$formattedTransactionRequestId-${System.nanoTime()}",

    /**
     *  An id that can uniquely identify a transaction. Typically an order id from a provider.
     */
    val correlationId: String? = null,

    /**
     * Additional metadata for the consumption
     */
    val metadata: Map<String, Any> = mapOf(),

    /**
     * Additional encrypted metadata for the consumption
     */
    val encryptedMetadata: Map<String, Any> = mapOf()
) {
    companion object {

        /**
         * Initialize the params used to consume a transaction request
         * Throws [IllegalArgumentException] if the amount is null and was not specified in the transaction request
         *
         * @param transactionRequest The transaction request to consume
         * @param amount The amount of token to transfer (down to subunit to unit)
         * @param address The address to use for the consumption
         * @param idempotencyToken The idempotency token to use for the consumption
         * @param correlationId An id that can uniquely identify a transaction. Typically an order id from a provider.
         * @param metadata Additional metadata for the consumption
         * @param encryptedMetadata Additional encrypted metadata for the consumption
         *
         * @return The [TransactionConsumptionParams] used for consume the a transaction request
         */
        fun create(
            transactionRequest: TransactionRequest,
            amount: BigDecimal? = null,
            address: String? = null,
            idempotencyToken: String = "${transactionRequest.id}-${System.nanoTime()}",
            correlationId: String? = null,
            metadata: Map<String, Any> = mapOf(),
            encryptedMetadata: Map<String, Any> = mapOf()
        ): TransactionConsumptionParams {
            require(transactionRequest.amount != null || amount != null) {
                "The transactionRequest amount or the amount of token to transfer should be provided"
            }

            return TransactionConsumptionParams(
                transactionRequest.formattedId,
                if (transactionRequest.amount?.stripTrailingZeros() == amount?.stripTrailingZeros()) null else amount,
                address,
                idempotencyToken,
                correlationId,
                metadata,
                encryptedMetadata
            )
        }

        /**
         * Initialize the params used to consume a transaction request
         * Throws [IllegalArgumentException] if the amount is null and was not specified in the transaction request
         *
         * @param transactionRequest The transaction request to consume
         * @param amount The amount of token to transfer (down to subunit to unit)
         * @param address The address to use for the consumption
         * @param idempotencyToken The idempotency token to use for the consumption
         * @param correlationId An id that can uniquely identify a transaction. Typically an order id from a provider.
         * @param metadata Additional metadata for the consumption
         * @param encryptedMetadata Additional encrypted metadata for the consumption
         *
         * @return The [TransactionConsumptionParams] used for consume the a transaction request
         */
        fun create(
            formattedTransactionRequestId: String,
            amount: BigDecimal? = null,
            address: String? = null,
            idempotencyToken: String = "$formattedTransactionRequestId-${System.nanoTime()}",
            correlationId: String? = null,
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
                encryptedMetadata
            )
        }
    }
}
