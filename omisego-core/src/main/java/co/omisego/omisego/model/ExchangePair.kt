package co.omisego.omisego.model

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

/**
 * Represents an ExchangePair.
 *
 * @param id The unique identifier of the exchange pair.
 * @param name The name of the pair (ex: ETH/BTC).
 * @param fromTokenId The 1st token id of the pair.
 * @param fromToken The 1st token of the pair.
 * @param toTokenId The 2nd token id of the pair.
 * @param toToken The 2nd token of the pair.
 * @param rate The rate between both tokens (token2/token1).
 * @param createdAt The creation date of the pair.
 * @param updatedAt The last update date of the pair.
 */
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
