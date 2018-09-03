package co.omisego.omisego.model.pagination

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class PaginationTest : GsonDelegator() {
    private val paginationFile by ResourceFile("pagination.json", "object")
    private val pagination: Pagination by lazy { gson.fromJson(paginationFile.readText(), Pagination::class.java) }

    @Test
    fun `pagination should be parsed correctly`() {
        with(pagination) {
            isFirstPage shouldEqualTo true
            isLastPage shouldEqualTo true
            perPage shouldEqualTo 10
            currentPage shouldEqualTo 1
        }
    }
}
