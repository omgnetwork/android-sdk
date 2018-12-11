package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.os.Parcelable
import co.omisego.omisego.model.pagination.Paginable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.math.BigDecimal
import java.util.Date

@Parcelize
data class TransactionExchange(
    val rate: BigDecimal?,
    val calculatedAt: Date?,
    val exchangePairId: String?,
    val exchangePair: ExchangePair?,
    val exchangeAccountId: String?,
    val exchangeAccount: Account?,
    val exchangeWalletAddress: String?,
    val exchangeWallet: Wallet?
) : Parcelable

@Parcelize
data class TransactionSource(
    val address: String,
    val amount: BigDecimal,
    val tokenId: String,
    val token: Token,
    val userId: String?,
    val user: User?,
    val accountId: String?,
    val account: Account?
) : Parcelable

/**
 * Represents a transaction.
 *
 * @param id The unique identifier of the transaction.
 * @param status The status of the transaction (pending, confirmed or failed).
 * @param from The source representing the source of the funds.
 * @param to The source representing the destination of the funds.
 * @param exchange Contains info of the exchange made during the transaction (if any).
 * @param metadata Additional metadata for the consumption.
 * @param encryptedMetadata Additional encrypted metadata for the consumption.
 * @param createdAt The creation date of the transaction.
 * @param error An APIError object if the transaction encountered an error
 */
@Parcelize
data class Transaction(
    val id: String,
    val status: Paginable.Transaction.TransactionStatus,
    val from: TransactionSource,
    val to: TransactionSource,
    val exchange: TransactionExchange,
    val metadata: @RawValue Map<String, Any>,
    val encryptedMetadata: @RawValue Map<String, Any>,
    val createdAt: Date,
    val error: APIError?
) : Parcelable
