package co.omisego.omisego.model.params

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 20/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.pagination.Paginable
import co.omisego.omisego.model.pagination.PaginableParams
import co.omisego.omisego.model.pagination.SortDirection

/**
 *  Represent a structure used to query a list of wallets
 */

data class UserWalletListParams internal constructor(
    /**
     * A provider user Id
     */
    val providerUserId: String,

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
     * - [Paginable.Wallet.SortableFields.NAME]
     * - [Paginable.Wallet.SortableFields.ADDRESS]
     * - [Paginable.Wallet.SortableFields.CREATED_AT]
     */
    override val sortBy: Paginable.Wallet.SortableFields = Paginable.Wallet.SortableFields.CREATED_AT,

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
     * See more at [Paginable.Wallet.SearchableFields]
     *
     * Note: Conflict with searchTerms, only use one of them.
     */
    override val searchTerm: String? = null,

    /**
     * A key-value map to search with the available fields
     * See more at [Paginable.Wallet.SearchableFields]
     *
     */
    val searchTerms: Map<Paginable.Wallet.SearchableFields, Any>? = null
): PaginableParams {
    companion object {
        fun create(
            providerUserId: String,
            page: Int = 1,
            perPage: Int = 10,
            sortBy: Paginable.Wallet.SortableFields = Paginable.Wallet.SortableFields.CREATED_AT,
            sortDir: SortDirection = SortDirection.DESCENDING
        ) = UserWalletListParams(
            providerUserId = providerUserId,
            page = page,
            perPage = perPage,
            sortBy = sortBy,
            sortDir = sortDir
        )
    }
}
