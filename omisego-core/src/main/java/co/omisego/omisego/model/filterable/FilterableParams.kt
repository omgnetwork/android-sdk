package co.omisego.omisego.model.filterable

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 21/11/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * A type that represents a filterable resource
 */
interface FilterableParams {
    val matchAny: List<Filter>?
    val matchAll: List<Filter>?
}
