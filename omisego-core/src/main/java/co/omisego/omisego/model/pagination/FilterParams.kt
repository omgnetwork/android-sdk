package co.omisego.omisego.model.pagination

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 21/11/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

data class Filter (
    val field: String,
    val comparator: String,
    val value: Any
)
