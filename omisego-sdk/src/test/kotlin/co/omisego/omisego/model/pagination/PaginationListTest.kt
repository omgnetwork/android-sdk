package co.omisego.omisego.model.pagination

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import com.google.gson.reflect.TypeToken
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class PaginationListTest : GsonDelegator() {
    private val paginationListFile by ResourceFile("json_list_response.json", "object")
    private val paginationList: PaginationList<String> by lazy {
        val typeToken = object : TypeToken<PaginationList<String>>() {}.type
        gson.fromJson<PaginationList<String>>(paginationListFile.readText(), typeToken)
    }

    @Test
    fun `pagination_list should be parsed correctly`() {
        with(paginationList) {
            data shouldBeInstanceOf List::class.java
            data.size shouldEqualTo 2
            data[0] shouldEqualTo "value_1"
            data[1] shouldEqualTo "value_2"
            pagination shouldBeInstanceOf Pagination::class.java
        }
    }
}