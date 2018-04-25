package co.omisego.omisego.model.transaction.request

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum

/**
 * The different types of request that can be generated
 */
enum class TransactionRequestType constructor(override val value: String) : OMGEnum {

    /* The initiator wants to receive a specified token */
    RECEIVE("receive");

    override fun toString(): String = value
}
