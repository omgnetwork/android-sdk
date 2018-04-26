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
    val amount: Double?,

    /**
     * The address to use for the consumption
     */
    val address: String?,

    /**
     * The id of the minted token to use for the request
     * In the case of a type "send", this will be the token that the consumer will receive
     * In the case of a type "receive" this will be the token that the consumer will send
     */
    val mintedTokenId: String?,

    /**
     * The idempotency token to use for the consumption
     */
    val idempotencyToken: String,

    /**
     *  An id that can uniquely identify a transaction. Typically an order id from a provider.
     */
    val correlationId: String?,

    /**
     * Additional metadata for the consumption
     */
    val metadata: Map<String, Any>,

    /**
     * Additional encrypted metadata for the consumption
     */
    val encryptedMetadata: Map<String, Any>
) {
    companion object {

        /**
         * Initialize the params used to consume a transaction request
         * Returns nil if the amount is nil and was not specified in the transaction request
         *
         * @param transactionRequest - The transaction request to consume
         * @param address - The address to use for the consumption
         * @param tokenId - The id of the minted token to use for the request
         * In the case of a type "send", this will be the token that the consumer will receive
         * In the case of a type "receive" this will be the token that the consumer will send
         * @param amount - The amount of minted token to transfer (down to subunit to unit)
         * @param idempotencyToken - The idempotency token to use for the consumption
         * @param correlationId - An id that can uniquely identify a transaction. Typically an order id from a provider
         * @param metadata - Additional metadata for the consumption
         *
         * @return A [TransactionConsumptionParams] to request to the API
         */
        fun init(
            transactionRequest: TransactionRequest,
            address: String?,
            tokenId: String?,
            amount: Double?,
            idempotencyToken: String,
            correlationId: String?,
            metadata: Map<String, Any> = mapOf(),
            encryptedMetadata: Map<String, Any> = mapOf()
        ): TransactionConsumptionParams? {
            if (transactionRequest.amount == null || amount == null) return null
            return TransactionConsumptionParams(
                transactionRequest.id,
                if (amount == transactionRequest.amount) amount else null,
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
