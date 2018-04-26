package co.omisego.omisego.model.transaction.consumption

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 25/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.transaction.request.TransactionRequest

/**
 * Represents a structure used to consume a transaction request
 */
data class TransactionConsumptionParams(
    /**
     * The id of the transaction request to be consumed
     */
    val transactionRequestId: String,

    /**
     * The amount of minted token to transfer (down to subunit to unit)
     */
    val amount: Double? = null,

    /**
     * The address to use for the consumption
     */
    val address: String? = null,

    /**
     * The id of the minted token to use for the request
     * In the case of a type "send", this will be the token that the consumer will receive
     * In the case of a type "receive" this will be the token that the consumer will send
     */
    val mintedTokenId: String? = null,

    /**
     * The idempotency token to use for the consumption
     */
    val idempotencyToken: String = "${transactionRequestId}-${System.currentTimeMillis()}",

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
         * Returns null if the amount is null and was not specified in the transaction request
         *
         * @param transactionRequest - The transaction request to consume
         * @param amount - The amount of minted token to transfer (down to subunit to unit)
         * @param metadata - Additional metadata for the consumption
         * @param encryptedMetadata - Additional encrypted metadata embedded with the request
         * @param idempotencyToken - The idempotency token to use for the consumption
         * @param address - The address to use for the consumption
         * @param tokenId - The id of the minted token to use for the request
         * In the case of a type "send", this will be the token that the consumer will receive
         * In the case of a type "receive" this will be the token that the consumer will send
         * @param correlationId - An id that can uniquely identify a transaction. Typically an order id from a provider
         *
         * @return A [TransactionConsumptionParams] to request to the API
         */
        fun init(
            transactionRequest: TransactionRequest,
            amount: Double? = null,
            metadata: Map<String, Any> = mapOf(),
            encryptedMetadata: Map<String, Any> = mapOf(),
            idempotencyToken: String = "${transactionRequest.id}-${System.nanoTime()}",
            address: String? = null,
            tokenId: String? = null,
            correlationId: String? = null
        ): TransactionConsumptionParams? {
            if (transactionRequest.amount == null && amount == null) return null
            return TransactionConsumptionParams(
                transactionRequest.id,
                if (amount == transactionRequest.amount) null else amount,
                address,
                tokenId,
                idempotencyToken,
                correlationId,
                metadata,
                encryptedMetadata
            )
        }
    }
}
