package co.omisego.omisego.model.params

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
     * All provided conditions are matched for a record to be returned
     */
    override val matchAll: List<Filter>? = null,

    /**
     * All provided conditions are matched for a record to be returned
     */
    override val matchAny: List<Filter>? = null
) : PaginableParams, FilterableParams {
    companion object {
        fun create(
            page: Int = 1,
            perPage: Int = 10,
            sortBy: Paginable.Token.SortableFields = Paginable.Token.SortableFields.CREATED_AT,
            sortDir: SortDirection = SortDirection.DESCENDING,
            matchAll: List<Filter>? = null,
            matchAny: List<Filter>? = null
        ) = TokenListParams(
            page = page,
            perPage = perPage,
            sortBy = sortBy,
            sortDir = sortDir,
            matchAll = matchAll,
            matchAny = matchAny
        )
    }
}
