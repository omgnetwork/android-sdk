package co.omisego.omisego.model.transaction.request

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.os.Parcelable
import co.omisego.omisego.constant.enums.OMGEnum
import co.omisego.omisego.model.Account
import co.omisego.omisego.model.Token
import co.omisego.omisego.model.User
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.transaction.consumption.TransactionConsumptionParams
import co.omisego.omisego.operation.Listenable
import co.omisego.omisego.websocket.SocketCustomEventListener
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
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
 * @param maxConsumption The maximum number of time that this request can be consumed. Default null (unlimited).
 * @param status The status of the request (valid or expired)
 * @param allowAmountOverride Allow or not the consumer to override the amount specified in the request
 * Note that if amount is nil and allowAmountOverride is false the init will fail and return null.
 * @param maxConsumptionsPerUser The maximum number of consumptions allowed per unique user. Default null (unlimited).
 * @param requireConfirmation A boolean indicating if the request needs a confirmation from the requester before being proceeded
 * @param expirationDate The date when the request will expire and not be consumable anymore. Default null (never expired).
 * @param expirationReason The reason why the request expired
 * @param consumptionLifetime The amount of time in millisecond during which a consumption is valid. Default null (forever).
 * @param createdAt The creation date of the request
 * @param expiredAt The date when the request expired
 * @param formattedId An id that can be encoded in a QR code and be used to retrieve the request later
 * @param metadata Additional metadata for the transaction request
 * @param encryptedMetadata Additional encrypted metadata for the transaction request
 */
@Parcelize
data class TransactionRequest(
    val id: String,
    val type: TransactionRequestType,
    val token: Token,
    val amount: BigDecimal?,
    val address: String?,
    val user: User?,
    val account: Account?,
    val correlationId: String?,
    override val socketTopic: SocketTopic<SocketCustomEventListener.TransactionRequestListener>,
    val maxConsumptions: Int?,
    val status: TransactionRequestStatus,
    val allowAmountOverride: Boolean,
    val maxConsumptionsPerUser: Int?,
    val requireConfirmation: Boolean,
    val expirationDate: Date?,
    val expirationReason: String?,
    val consumptionLifetime: Int?,
    val createdAt: Date?,
    val expiredAt: Date?,
    val formattedId: String,
    val metadata: @RawValue Map<String, Any>,
    val encryptedMetadata: @RawValue Map<String, Any>
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
