package co.omisego.omisego.model.params.client

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.TransactionRequestType
import java.math.BigDecimal
import java.util.Date

/**
 * Represents a structure used to generate a transaction request
 *
 * @param type The type of transaction to be generated (send of receive)
 * @param tokenId The unique identifier of the token to use for the request
 * In the case of a type "send", this will be the token taken from the requester
 * In the case of a type "receive" this will be the token received by the requester
 * @param amount The amount of token to use for the transaction (down to subunit to unit)
 * This amount can be either inputted when generating or consuming a transaction request.
 * @param address The address specifying where the transaction should be sent to.
 * If not specified, the current user's primary wallet address will be used.
 * @param requireConfirmation A boolean indicating if the request needs a confirmation from the requester before being proceeded. Default true.
 * @param allowAmountOverride Allow or not the consumer to override the amount specified in the request
 * This needs to be true if the amount is not specified. Default true.
 * @param correlationId An id that can uniquely identify a transaction. Typically an order id from a provider.
 * @param maxConsumptions The maximum number of time that this request can be consumed. Default null (unlimited).
 * @param maxConsumptionsPerUser The maximum number of consumptions allowed per unique user. Default null (unlimited).
 * @param consumptionLifetime The amount of time in millisecond during which a consumption is valid. Default null (forever).
 * @param expirationDate The date when the request will expire and not be consumable anymore. Default null (forever).
 * @param metadata Additional metadata embedded with the request
 * @param encryptedMetadata Additional encrypted metadata embedded with the request
 */
data class TransactionRequestCreateParams(
    val type: TransactionRequestType = TransactionRequestType.RECEIVE,
    val tokenId: String,
    val amount: BigDecimal? = null,
    val address: String? = null,
    val requireConfirmation: Boolean = true,
    val allowAmountOverride: Boolean = true,
    val correlationId: String? = null,
    val maxConsumptions: Int? = null,
    val maxConsumptionsPerUser: Int? = null,
    val consumptionLifetime: Int? = null,
    val expirationDate: Date? = null,
    val metadata: Map<String, Any> = mapOf(),
    val encryptedMetadata: Map<String, Any> = mapOf()
) {

    init {
        require(allowAmountOverride || amount != null) {
            "The amount cannot be null if the allowAmountOverride is false"
        }
    }
}
