package co.omisego.omisego.model.transaction.request

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.MintedToken

/**
 * Represents a transaction request
 * 
 */
data class TransactionRequest(
    val id: String,
    val type: TransactionRequestType,
    val mintedToken: MintedToken,
    val status: String,
    val amount: Double?,
    val address: String?
)
