package co.omisego.omisego.model.transaction.consumption

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 25/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.transaction.request.TransactionRequest
import java.math.BigDecimal

/**
 * Represents a structure used to consume a transaction request
 */
data class TransactionConsumptionParams(
    /**
     * The transaction request to be consumed
     */
    private val transactionRequest: TransactionRequest,

    /**
     * The amount of minted token to transfer (down to subunit to unit)
     */
    var amount: BigDecimal? = null,

    /**
     * The address to use for the consumption
     */
    val address: String? = null,

    /**
     * The id of the minted token to use for the request
     * In the case of a type "send", this will be the token that the consumer will receive
     * In the case of a type "receive" this will be the token that the consumer will send
     */
    val tokenId: String? = null,

    /**
     * The idempotency token to use for the consumption
     */
    val idempotencyToken: String = "${transactionRequest.id}-${System.nanoTime()}",

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

    /**
     * Initialize the params used to consume a transaction request
     * Returns null if the amount is null and was not specified in the transaction request
     */
    init {
        require(transactionRequest.amount != null || amount != null) {
            "The transactionRequest amount or the amount of minted token to transfer should be provided"
        }

        amount = if (transactionRequest.amount == amount) null else amount
    }
}
