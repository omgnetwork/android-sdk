package co.omisego.omisego.model

import co.omisego.omisego.model.params.client.TransactionConsumptionParams
import java.math.BigDecimal

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 25/10/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * An extension function that converts the [TransactionRequest] to the [TransactionConsumptionParams] easily
 *
 * @param amount The amount of token to transfer (down to subunit to unit)
 * @param address The address to use for the consumption
 * In the case of a type "send", this will be the token that the consumer will receive
 * In the case of a type "receive" this will be the token that the consumer will send
 * @param idempotencyToken The idempotency token to use for the consumption
 * @param correlationId An id that can uniquely identify a transaction. Typically an order id from a provider.
 * @param metadata Additional metadata for the consumption
 * @param encryptedMetadata Additional encrypted metadata for the consumption
 *
 * @return The [TransactionConsumptionParams] used for consume the a transaction request
 */
fun TransactionRequest.toTransactionConsumptionParams(
    amount: BigDecimal? = null,
    address: String? = null,
    idempotencyToken: String = "${this.id}-${System.nanoTime()}",
    correlationId: String? = null,
    metadata: Map<String, Any> = mapOf(),
    encryptedMetadata: Map<String, Any> = mapOf()
): TransactionConsumptionParams =
    TransactionConsumptionParams.create(
        this,
        amount,
        address,
        idempotencyToken,
        correlationId,
        metadata,
        encryptedMetadata
    )
