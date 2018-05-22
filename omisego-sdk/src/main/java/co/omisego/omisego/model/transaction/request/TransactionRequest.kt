package co.omisego.omisego.model.transaction.request

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum
import co.omisego.omisego.model.Token
import co.omisego.omisego.model.User
import co.omisego.omisego.model.transaction.consumption.TransactionConsumptionParams
import co.omisego.omisego.operation.Listenable
import java.math.BigDecimal
import java.util.Date

/**
 * The different types of request that can be generated
 */
enum class TransactionRequestType constructor(override val value: String) : OMGEnum {

    /* The initiator wants to receive a specified token */
    RECEIVE("receive"),

    /* The initiator wants to send a specified token */
    SEND("send");

    override fun toString(): String = value
}

/**
 * The status of the transaction request
 */
enum class TransactionRequestStatus constructor(override val value: String) : OMGEnum {
    /* The transaction request is expired and can't be consumed */
    EXPIRED("expired"),

    /* The transaction request is valid and ready to be consumed */
    VALID("valid");

    override fun toString(): String = value
}

/**
 * Represents a transaction request
 *
 */
data class TransactionRequest(
    /**
     * The unique identifier of the request
     */
    val id: String,

    /**
     * The type of the request (send of receive)
     */
    val type: TransactionRequestType,

    /**
     * The token for the request
     * In the case of a type "send", this will be the token taken from the requester
     * In the case of a type "receive" this will be the token received by the requester
     */
    val token: Token,

    /**
     * The amount of token to use for the transaction (down to subunit to unit)
     * This amount needs to be either specified by the requester or the consumer
     */
    val amount: BigDecimal?,

    /**
     * The address from which to send or receive the tokens
     */
    val address: String?,

    /**
     * The user that initiated the request
     */
    val user: User?,

    /**
     * The topic which can be listened in order to receive events regarding this request
     */
    override val socketTopic: String,

    /**
     * The maximum number of time that this request can be consumed
     */
    val maxConsumption: Int?,

    /**
     * The status of the request (valid or expired)
     */
    val status: TransactionRequestStatus,

    /**
     * Allow or not the consumer to override the amount specified in the request
     */
    val allowAmountOverride: Boolean,

    /**
     * A boolean indicating if the request needs a confirmation from the requester before being proceeded
     */
    val requireConfirmation: Boolean,

    /**
     * The date when the request will expire and not be consumable anymore
     */
    val expirationDate: Date,

    /**
     * The reason why the request expired
     */
    val expirationReason: String?,

    /**
     * The amount of time in millisecond during which a consumption is valid
     */
    val consumptionLifetime: Int?,

    /**
     * The creation date of the request
     */
    val createdAt: Date?,

    /**
     * The date when the request expired
     */
    val expiredAt: Date?
) : Listenable

/**
 * An extension function that converts the [TransactionRequest] to the [TransactionConsumptionParams] easily
 *
 * @param amount The amount of token to transfer (down to subunit to unit)
 * @param address The address to use for the consumption
 * @param tokenId The id of the token to use for the request
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
    tokenId: String? = null,
    idempotencyToken: String = "${this.id}-${System.nanoTime()}",
    correlationId: String? = null,
    metadata: Map<String, Any> = mapOf(),
    encryptedMetadata: Map<String, Any> = mapOf()
): TransactionConsumptionParams? =
    TransactionConsumptionParams.create(
        this,
        amount,
        address,
        tokenId,
        idempotencyToken,
        correlationId,
        metadata,
        encryptedMetadata
    )
