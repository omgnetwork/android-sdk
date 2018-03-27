package co.omisego.omisego.model.transaction.request

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum
import co.omisego.omisego.custom.gson.OMGEnumAdapter
import com.google.gson.annotations.JsonAdapter

@JsonAdapter(OMGEnumAdapter::class)
enum class TransactionRequestType constructor(override val value: String) : OMGEnum {
    RECEIVE("receive");

    override fun toString(): String = value
}
