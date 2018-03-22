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
data class TransactionListParams(
        /**
         * A page number
         */
        val page: Int,

        /**
         * A number of results per page.
         */
        val perPage: Int,

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
        val sortBy: Paginable.Transaction.SortableFields,

        /**
         * The desired sort direction
         *
         * The available values are
         * - [SortDirection.ASCENDING]
         * - [SortDirection.DESCENDING]
         */
        val sortDirection: SortDirection,

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
         * An optional address that belongs to the current user (primary address by default)
         */
        val address: String? = null
)
