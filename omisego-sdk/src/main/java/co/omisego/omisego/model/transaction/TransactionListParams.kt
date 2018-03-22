package co.omisego.omisego.model.transaction

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

class TransactionListParams {
    /**
     * A page number
     */
    private var page: Int = 1

    /**
     * A number of results per page.
     */
    private var perPage: Int = 10

    /**
     * The sorting field
     *
     * The available values are
     * - [Paginable.Transaction.SortableFields.ID]
     * - [Paginable.Transaction.SortableFields.STATUS]
     * - [Paginable.Transaction.SortableFields.FROM]
     * - [Paginable.Transaction.SortableFields.CREATED_AT]
     * - [Paginable.Transaction.SortableFields.UPDATED_AT]
     */
    private var sortBy: Paginable.Transaction.SortableFields = Paginable.Transaction.SortableFields.CREATED_AT

    /**
     * The desired sort direction
     *
     * The available values are
     * - [SortDirection.ASCENDING]
     * - [SortDirection.DESCENDING]
     */
    private var sortDirection: SortDirection = SortDirection.DESCENDING

    /**
     * A term to search for in all of the searchable fields.
     * See more at [Paginable.Transaction.SearchableFields]
     *
     * Note: Conflict with searchTerms, only use one of them.
     */
    private var searchTerm: String? = null

    /**
     * A key-value map to search with the available fields
     * See more at [Paginable.Transaction.SearchableFields]
     *
     */
    private var searchTerms: Map<Paginable.Transaction.SearchableFields, Any>? = null

    /**
     * An optional address that belongs to the current user (primary address by default)
     */
    private var address: String? = null

    companion object {
        fun create(
                page: Int = 1,
                perPage: Int = 10,
                sortBy: Paginable.Transaction.SortableFields = Paginable.Transaction.SortableFields.CREATED_AT,
                sortDirection: SortDirection = SortDirection.DESCENDING,
                searchTerm: String? = null,
                address: String? = null
        ) = TransactionListParams().apply {
            this.page = page
            this.perPage = perPage
            this.sortBy = sortBy
            this.sortDirection = sortDirection
            this.searchTerm = searchTerm
            this.address = address
        }

        fun create(
                page: Int = 1,
                perPage: Int = 10,
                sortBy: Paginable.Transaction.SortableFields = Paginable.Transaction.SortableFields.CREATED_AT,
                sortDirection: SortDirection = SortDirection.DESCENDING,
                searchTerms: Map<Paginable.Transaction.SearchableFields, Any>? = null,
                address: String? = null
        ) = TransactionListParams().apply {
            this.page = page
            this.perPage = perPage
            this.sortBy = sortBy
            this.sortDirection = sortDirection
            this.searchTerms = searchTerms
            this.address = address
        }
    }
}

