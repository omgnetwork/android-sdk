package co.omisego.omisego.model.transaction.request

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.os.Parcelable
import co.omisego.omisego.constant.enums.OMGEnum
import co.omisego.omisego.model.Token
import co.omisego.omisego.model.User
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.transaction.consumption.TransactionConsumptionParams
import kotlinx.android.parcel.Parcelize
import co.omisego.omisego.operation.Listenable
import co.omisego.omisego.websocket.SocketCustomEventListener
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
 * @param id The unique identifier of the request
 * @param type The type of the request (send of receive)
 * @param token The token for the request
 * In the case of a type "send", this will be the token taken from the requester
 * In the case of a type "receive" this will be the token received by the requester
 * @param amount The amount of token to use for the transaction (down to subunit to unit)
 * This amount needs to be either specified by the requester or the consumer
 * @param address The address from which to send or receive the tokens
 * @param user The user that initiated the request
 * @param socketTopic The topic which can be listened in order to receive events regarding this request
 * @param maxConsumption The maximum number of time that this request can be consumed
 * @param status The status of the request (valid or expired)
 * @param allowAmountOverride Allow or not the consumer to override the amount specified in the request
 * @param requireConfirmation A boolean indicating if the request needs a confirmation from the requester before being proceeded
 * @param expirationDate The date when the request will expire and not be consumable anymore
 * @param expirationReason The reason why the request expired
 * @param consumptionLifetime The amount of time in millisecond during which a consumption is valid
 * @param createdAt The creation date of the request
 * @param expiredAt The date when the request expired
 */
@Parcelize
data class TransactionRequest(
    val id: String,
    val type: TransactionRequestType,
    val token: Token,
    val amount: BigDecimal?,
    val address: String?,
    val user: User?,
    override val socketTopic: SocketTopic<SocketCustomEventListener.TransactionRequestListener>,
    val maxConsumption: Int?,
    val status: TransactionRequestStatus,
    val allowAmountOverride: Boolean,
    val requireConfirmation: Boolean,
    val expirationDate: Date,
    val expirationReason: String?,
    val consumptionLifetime: Int?,
    val createdAt: Date?,
    val expiredAt: Date?
) : Parcelable, Listenable<SocketCustomEventListener.TransactionRequestListener>

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
