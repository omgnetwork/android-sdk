package co.omisego.omisego.model.transaction

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.os.Parcelable
import co.omisego.omisego.model.Token
import co.omisego.omisego.model.pagination.Paginable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.math.BigDecimal
import java.util.Date

@Parcelize
data class TransactionExchange(
    val rate: BigDecimal,
    val calculatedAt: Date?,
    val exchangePairId: String?
) : Parcelable

@Parcelize
data class TransactionSource(
    val address: String,
    val amount: BigDecimal,
    val token: Token
) : Parcelable

@Parcelize
data class Transaction(
    val id: String,
    val status: TransactionStatus,
    val from: TransactionSource,
    val to: TransactionSource,
    val exchange: TransactionExchange,
    val metadata: @RawValue Map<String, Any>,
    val encryptedMetadata: @RawValue Map<String, Any>,
    val createdAt: Date
) : Paginable.Transaction(), Parcelable
