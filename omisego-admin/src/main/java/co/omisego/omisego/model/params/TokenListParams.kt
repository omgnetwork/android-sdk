package co.omisego.omisego.model.params

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 20/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.pagination.Filter
import co.omisego.omisego.model.pagination.Paginable
import co.omisego.omisego.model.pagination.PaginableParams
import co.omisego.omisego.model.pagination.SortDirection

/**
 *  Represent a structure used to query a list of tokens
 */

data class TokenListParams internal constructor(
    /**
     * A page number
     */
    override val page: Int = 1,

    /**
     * A number of results per page.
     */
    override val perPage: Int = 10,

    /**
     * The sorting field
     *
     * The available values are
     * - [Paginable.Token.SortableFields.NAME]
     * - [Paginable.Token.SortableFields.SYMBOL]
     * - [Paginable.Token.SortableFields.CREATED_AT]
     */
    override val sortBy: Paginable.Token.SortableFields = Paginable.Token.SortableFields.CREATED_AT,

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
     * See more at [Paginable.Token.SearchableFields]
     *
     * Note: Conflict with searchTerms, only use one of them.
     */
    override val searchTerm: String? = null,

    override val matchAll: List<Filter>? = null,

    override val matchAny: List<Filter>? = null
) : PaginableParams {
    companion object {
        fun create(
            page: Int = 1,
            perPage: Int = 10,
            sortBy: Paginable.Token.SortableFields = Paginable.Token.SortableFields.CREATED_AT,
            sortDir: SortDirection = SortDirection.DESCENDING,
            searchTerm: String? = null,
            matchAll: List<Filter>? = null,
            matchAny: List<Filter>? = null
        ) = TokenListParams(
            page = page,
            perPage = perPage,
            sortBy = sortBy,
            sortDir = sortDir,
            searchTerm = searchTerm,
            matchAll = matchAll,
            matchAny = matchAny
        )

    }
}
