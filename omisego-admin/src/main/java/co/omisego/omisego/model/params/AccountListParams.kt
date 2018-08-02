package co.omisego.omisego.model.params

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

data class AccountListParams internal constructor(
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
     * - [Paginable.Account.SortableFields.ID]
     * - [Paginable.Account.SortableFields.NAME]
     * - [Paginable.Account.SortableFields.DESCRIPTION]
     * - [Paginable.Account.SortableFields.CREATED_AT]
     * - [Paginable.Account.SortableFields.UPDATED_AT]
     */
    val sortBy: Paginable.Account.SortableFields = Paginable.Account.SortableFields.CREATED_AT,

    /**
     * The desired sort direction
     *
     * The available values are
     * - [SortDirection.ASCENDING]
     * - [SortDirection.DESCENDING]
     */
    val sortDir: SortDirection = SortDirection.DESCENDING,

    /**
     * A term to search for in all of the searchable fields.
     * See more at [Paginable.Account.SearchableFields]
     *
     * Note: Conflict with searchTerms, only use one of them.
     */
    val searchTerm: String? = null,

    /**
     * A key-value map to search with the available fields
     * See more at [Paginable.Account.SearchableFields]
     *
     */
    val searchTerms: Map<Paginable.Account.SearchableFields, Any>? = null
) {
    companion object {
        fun create(
            page: Int = 1,
            perPage: Int = 10,
            sortBy: Paginable.Account.SortableFields = Paginable.Account.SortableFields.CREATED_AT,
            sortDir: SortDirection = SortDirection.DESCENDING,
            searchTerm: String? = null
        ) = AccountListParams(
            page = page,
            perPage = perPage,
            sortBy = sortBy,
            sortDir = sortDir,
            searchTerm = searchTerm
        )

        fun create(
            page: Int = 1,
            perPage: Int = 10,
            sortBy: Paginable.Account.SortableFields = Paginable.Account.SortableFields.CREATED_AT,
            sortDir: SortDirection = SortDirection.DESCENDING,
            searchTerms: Map<Paginable.Account.SearchableFields, Any>? = null
        ) = AccountListParams(
            page = page,
            perPage = perPage,
            sortBy = sortBy,
            sortDir = sortDir,
            searchTerms = searchTerms
        )
    }
}
