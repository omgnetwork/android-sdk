package co.omisego.omisego.model.pagination

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 21/11/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

interface PaginableParams {
    val page: Int
    val perPage: Int
    val searchTerm: String?
    val sortDir: SortDirection
    val sortBy: Paginable.SortableFields
}
