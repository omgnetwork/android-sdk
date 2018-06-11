package co.omisego.omisego.model.transaction.list

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 20/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.pagination.Paginable
import co.omisego.omisego.model.pagination.SortDirection

/**
 *  Represent a structure used to query a list of transactions
 */

data class TransactionListParams internal constructor(
    /**
     * A page number
     */
    val page: Int = 1,

    /**
     * A number of results per page.
     */
    val perPage: Int = 10,

    /**
     * The sorting field
     *
     * The available values are
     * - [Paginable.Transaction.SortableFields.ID]
     * - [Paginable.Transaction.SortableFields.STATUS]
     * - [Paginable.Transaction.SortableFields.FROM]
     * - [Paginable.Transaction.SortableFields.CREATED_AT]
     */
    val sortBy: Paginable.Transaction.SortableFields = Paginable.Transaction.SortableFields.CREATED_AT,

    /**
     * The desired sort direction
     *
     * The available values are
     * - [SortDirection.ASCENDING]
     * - [SortDirection.DESCENDING]
     */
    val sortDirection: SortDirection = SortDirection.DESCENDING,

    /**
     * A term to search for in all of the searchable fields.
     * See more at [Paginable.Transaction.SearchableFields]
     *
     * Note: Conflict with searchTerms, only use one of them.
     */
    val searchTerm: String? = null,

    /**
     * A key-value map to search with the available fields
     * See more at [Paginable.Transaction.SearchableFields]
     *
     */
    val searchTerms: Map<Paginable.Transaction.SearchableFields, Any>? = null,

    /**
     * An optional wallet address that belongs to the current user (primary address by default)
     */
    val address: String? = null
) {
    companion object {
        fun create(
            page: Int = 1,
            perPage: Int = 10,
            sortBy: Paginable.Transaction.SortableFields = Paginable.Transaction.SortableFields.CREATED_AT,
            sortDirection: SortDirection = SortDirection.DESCENDING,
            searchTerm: String? = null,
            address: String? = null
        ) = TransactionListParams(
            page = page,
            perPage = perPage,
            sortBy = sortBy,
            sortDirection = sortDirection,
            searchTerm = searchTerm,
            address = address
        )

        fun create(
            page: Int = 1,
            perPage: Int = 10,
            sortBy: Paginable.Transaction.SortableFields = Paginable.Transaction.SortableFields.CREATED_AT,
            sortDirection: SortDirection = SortDirection.DESCENDING,
            searchTerms: Map<Paginable.Transaction.SearchableFields, Any>? = null,
            address: String? = null
        ) = TransactionListParams(
            page = page,
            perPage = perPage,
            sortBy = sortBy,
            sortDirection = sortDirection,
            searchTerms = searchTerms,
            address = address
        )
    }
}
