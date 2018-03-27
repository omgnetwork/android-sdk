package co.omisego.omisego.model.transaction.request

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
data class TransactionRequestCreateParams(
    val type: TransactionRequestType = TransactionRequestType.RECEIVE,
    val tokenId: String,
    val amount: Int? = null,
    val address: String? = null,
    val correlationId: String? = null
)
