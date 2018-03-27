package co.omisego.omisego.model.transaction.request

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * Represents a structure used to generate a transaction request
 *
 */
data class TransactionRequestCreateParams(

    /* The type of transaction to be generated */
    val type: TransactionRequestType = TransactionRequestType.RECEIVE,

    /* The id of the desired token */
    val tokenId: String,

    /* The amount of token to receive
       This amount can be either inputted when generating or consuming a transaction request. */
    val amount: Int? = null,

    /* The address specifying where the transaction should be sent to.
       If not specified, the current user's primary address will be used. */
    val address: String? = null,

    /* An id that can uniquely identify a transaction. Typically an order id from a provider. */
    val correlationId: String? = null
)
