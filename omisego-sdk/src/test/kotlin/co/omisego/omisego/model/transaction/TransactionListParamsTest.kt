package co.omisego.omisego.model.transaction

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 20/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.pagination.Paginable.Transaction.SearchableFields
import co.omisego.omisego.model.pagination.Paginable.Transaction.SortableFields
import co.omisego.omisego.model.pagination.SortDirection
import co.omisego.omisego.model.transaction.list.TransactionListParams
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.amshove.kluent.shouldEqual
import org.junit.Before
import kotlin.test.Test

class TransactionListParamsTest {
    private lateinit var gson: Gson
    @Before
    fun setUp() {
        gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
    }

    @Test
    fun `TransactionListParams should be added successfully`() {
        val transactionListParams = TransactionListParams(1,
            10,
            SortableFields.FROM,
            SortDirection.ASCENDING,
            "test",
            mapOf(SearchableFields.STATUS to "completed", SearchableFields.ID to "1234"),
            "address:1234"
        )

        val expected = """
            {"page":1,"per_page":10,"sort_by":"from","sort_direction":"asc","search_term":"test","search_terms":{"status":"completed","id":"1234"},"address":"address:1234"}
        """.trimIndent()

        val actual = gson.toJson(transactionListParams)

        actual shouldEqual expected
    }
}
