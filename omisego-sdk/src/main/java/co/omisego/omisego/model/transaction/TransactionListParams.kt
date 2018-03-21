package co.omisego.omisego.model.transaction

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 20/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.pagination.Paginable
import co.omisego.omisego.model.pagination.SortDirection

data class TransactionListParams(
    val page: Int,
    val perPage: Int,
    val sortBy: Paginable.Transaction.SortableFields,
    val sortDirection: SortDirection,
    val searchTerm: String? = null,
    val searchTerms: Map<Paginable.Transaction.SearchableFields, Any>? = null,
    val address: String? = null
)
