package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/8/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import java.math.BigDecimal
import java.util.Date

data class TransactionCalculation(
    val fromAmount: BigDecimal,
    val fromTokenId: String,
    val toAmount: BigDecimal,
    val toTokenId: String,
    val exchangePair: ExchangePair,
    val calculatedAt: Date
)
