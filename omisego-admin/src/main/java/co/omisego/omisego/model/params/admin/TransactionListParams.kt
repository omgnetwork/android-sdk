package co.omisego.omisego.model.params.admin

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 20/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.filterable.Filter
import co.omisego.omisego.model.filterable.FilterableParams
import co.omisego.omisego.model.pagination.Paginable
import co.omisego.omisego.model.pagination.PaginableParams
import co.omisego.omisego.model.pagination.SortDirection

/**
 *  Represent a structure used to query a list of transactions
 */

data class TransactionListParams internal constructor(

    override val page: Int = 1,

    override val perPage: Int = 10,

    /**
     * The sorting field
     *
     * The available values are
     * - [Paginable.Transaction.SortableFields.ID]
     * - [Paginable.Transaction.SortableFields.STATUS]
     * - [Paginable.Transaction.SortableFields.FROM]
     * - [Paginable.Transaction.SortableFields.CREATED_AT]
     */
    override val sortBy: Paginable.Transaction.SortableFields = Paginable.Transaction.SortableFields.CREATED_AT,

    /**
     * The desired sort direction
     *
     * The available values are
     * - [SortDirection.ASCENDING]
     * - [SortDirection.DESCENDING]
     */
    override val sortDir: SortDirection = SortDirection.DESCENDING,

    /**
     * A term to search for in all of the searchable fields.
     */
    override val searchTerm: String? = null,

    override val matchAll: List<Filter>? = null,

    override val matchAny: List<Filter>? = null,

    /**
     * An optional wallet address that belongs to the current user (primary address by default)
     */
    val address: String? = null
) : FilterableParams, PaginableParams  {

    companion object {
        fun create(
            page: Int = 1,
            perPage: Int = 10,
            sortBy: Paginable.Transaction.SortableFields = Paginable.Transaction.SortableFields.CREATED_AT,
            sortDir: SortDirection = SortDirection.DESCENDING,
            searchTerm: String? = null,
            matchAll: List<Filter>? = null,
            matchAny: List<Filter>? = null,
            address: String? = null
        ) = TransactionListParams(
            page = page,
            perPage = perPage,
            sortBy = sortBy,
            sortDir = sortDir,
            searchTerm = searchTerm,
            matchAny = matchAny,
            matchAll = matchAll,
            address = address
        )
    }
}
