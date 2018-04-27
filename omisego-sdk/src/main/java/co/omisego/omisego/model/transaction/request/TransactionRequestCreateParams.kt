package co.omisego.omisego.model.transaction.request

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import java.math.BigDecimal
import java.util.Date

/**
 * Represents a structure used to generate a transaction request
 *
 */
data class TransactionRequestCreateParams(

    /**
     * The type of transaction to be generated (send of receive)
     */
    val type: TransactionRequestType = TransactionRequestType.RECEIVE,

    /**
     * The unique identifier of the minted token to use for the request
     * In the case of a type "send", this will be the token taken from the requester
     * In the case of a type "receive" this will be the token received by the requester
     */
    val tokenId: String,

    /**
     * The amount of minted token to use for the transaction (down to subunit to unit)
     * This amount can be either inputted when generating or consuming a transaction request.
     */
    val amount: BigDecimal? = null,

    /**
     * The address specifying where the transaction should be sent to.
     * If not specified, the current user's primary address will be used.
     */
    val address: String? = null,

    /**
     * A boolean indicating if the request needs a confirmation from the requester before being proceeded
     */
    val requireConfirmation: Boolean = true,

    /**
     * Allow or not the consumer to override the amount specified in the request
     * This needs to be true if the amount is not specified
     */
    val allowAmountOverride: Boolean = false,

    /**
     * Additional metadata embedded with the request
     */
    val metadata: Map<String, Any> = mapOf(),

    /**
     * Additional encrypted metadata embedded with the request
     */
    val encryptedMetadata: Map<String, Any> = mapOf(),

    /**
     * An id that can uniquely identify a transaction. Typically an order id from a provider.
     */
    val correlationId: String? = null,

    /**
     * The maximum number of time that this request can be consumed
     */
    val maxConsumptions: Int? = null,

    /**
     * The amount of time in millisecond during which a consumption is valid
     */
    val consumptionLifetime: Int? = null,

    /**
     * The date when the request will expire and not be consumable anymore
     */
    val expirationDate: Date? = null
) {

    init {
        require(allowAmountOverride || amount != null) {
            "allowAmountOverride "
        }
    }

    companion object {
        fun init(
            type: TransactionRequestType,
            tokenId: String,
            amount: BigDecimal? = null,
            address: String? = null,
            requireConfirmation: Boolean = false,
            allowAmountOverride: Boolean = true,
            correlationId: String? = null,
            maxConsumptions: Int? = null,
            consumptionLifetime: Int? = null,
            expirationDate: Date? = null,
            metadata: Map<String, Any> = mapOf(),
            encryptedMetadata: Map<String, Any> = mapOf()
        ): TransactionRequestCreateParams? {
            if (!allowAmountOverride && amount == null) return null
            return TransactionRequestCreateParams(
                type,
                tokenId,
                amount,
                address,
                requireConfirmation,
                allowAmountOverride,
                metadata,
                encryptedMetadata,
                correlationId,
                maxConsumptions,
                consumptionLifetime,
                expirationDate
            )
        }
    }
}
