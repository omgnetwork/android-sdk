package co.omisego.omisego.admin.model.params

import java.math.BigDecimal

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/8/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

data class TransactionCalculateParams(
    val fromTokenId: String,
    val toTokenId: String,
    val fromAmount: BigDecimal
)
