package co.omisego.omisego.model.transaction

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/6/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.os.Parcelable
import co.omisego.omisego.model.Token
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.util.Date

@Parcelize
data class ExchangePair(
    val id: String,
    val name: String,
    val fromTokenId: String,
    val fromToken: Token,
    val toTokenId: String,
    val toToken: Token,
    val rate: BigDecimal,
    val createdAt: Date,
    val updatedAt: Date
) : Parcelable
