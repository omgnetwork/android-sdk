package co.omisego.omisego.model.transaction.consumption

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 25/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.accounts.Account
import co.omisego.omisego.OMGAPIClient
import co.omisego.omisego.constant.enums.OMGEnum
import co.omisego.omisego.custom.retrofit2.adapter.OMGCall
import co.omisego.omisego.model.Token
import co.omisego.omisego.model.User
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.model.transaction.list.Transaction
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.operation.Listenable
import co.omisego.omisego.websocket.SocketCustomEventListener
import java.math.BigDecimal
import java.util.Date

/**
 * Represents transaction consumption statuses.
 *
 * - pending: The transaction consumption is pending validation
 * - confirmed: The transaction was consumed
 * - failed: The transaction failed to be consumed
 */
enum class TransactionConsumptionStatus constructor(override val value: String) : OMGEnum {
    PENDING("pending"),
    CONFIRMED("confirmed"),
    APPROVED("approved"),
    REJECTED("rejected"),
    FAILED("failed"),
    UNKNOWN("unknown");

    override fun toString(): String = value
}

data class TransactionConsumption(
    /**
     * The unique identifier of the consumption
     */
    val id: String,

    /**
     * The status of the consumption (pending, confirmed or failed)
     */
    val status: TransactionConsumptionStatus,

    /**
     * The amount of token to transfer (down to subunit to unit)
     */
    val amount: BigDecimal,

    /**
     * The token for the request
     * In the case of a type "send", this will be the token that the consumer will receive
     * In the case of a type "receive" this will be the token that the consumer will send
     */
    val token: Token,

    /**
     * An id that can uniquely identify a transaction. Typically an order id from a provider.
     */
    val correlationId: String?,

    /**
     * The idempotency token of the consumption
     */
    val idempotencyToken: String,

    /**
     * The transaction generated by this consumption (this will be null until the consumption is confirmed)
     */
    val transaction: Transaction?,

    /**
     * The address used for the consumption
     */
    val address: String,

    /**
     * The user that initiated the consumption
     */
    val user: User?,

    /**
     * The account that initiated the consumption
     */
    val account: Account?,

    /**
     * The transaction request to be consumed
     */
    val transactionRequest: TransactionRequest,

    /**
     * The topic which can be listened in order to receive events regarding this consumption
     */
    override val socketTopic: SocketTopic<SocketCustomEventListener.TransactionConsumptionListener>,

    /**
     * The creation date of the consumption
     */
    val createdAt: Date,

    /**
     * The date when the consumption will expire
     */
    val expirationDate: Date?,

    /**
     * The date when the consumption got approved
     */
    val approvedAt: Date?,

    /**
     * The date when the consumption got rejected
     */
    val rejectedAt: Date?,

    /**
     * The date when the consumption got confirmed
     */
    val confirmedAt: Date?,

    /**
     * The date when the consumption failed
     */
    val failedAt: Date?,

    /**
     * The date when the consumption expired
     */
    val expiredAt: Date?,

    /**
     * Additional metadata for the consumption
     */
    val metadata: Map<String, Any>,

    /**
     * Additional encrypted metadata for the consumption
     */
    val encryptedMetadata: Map<String, Any>
) : Listenable<SocketCustomEventListener.TransactionConsumptionListener>, SocketReceive.SocketData {
    override fun equals(other: Any?): Boolean {
        return other is TransactionConsumption && other.id == id
    }
}

/**
 * An extension function that uses the id from `TransactionConsumption` object to approve the transaction
 *
 * @param omgAPIClient the [OMGAPIClient] object in your application to be used to approve the transaction
 * @return The [OMGCall<TransactionConsumption>] object that you need to call enqueue method on to actually perform the request to the API
 */
fun TransactionConsumption.approve(omgAPIClient: OMGAPIClient): OMGCall<TransactionConsumption> =
    omgAPIClient.approveTransactionConsumption(TransactionConsumptionActionParams(this.id))

/**
 * An extension function that uses the id from `TransactionConsumption` object to reject the transaction
 *
 * @param omgAPIClient the [OMGAPIClient] object in your application to be used to reject the transaction
 * @return The [OMGCall<TransactionConsumption>] object that you need to call enqueue method on to actually perform the request to the API
 */
fun TransactionConsumption.reject(omgAPIClient: OMGAPIClient) =
    omgAPIClient.rejectTransactionConsumption(TransactionConsumptionActionParams(this.id))
