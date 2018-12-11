package co.omisego.omisego.client.model.params

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 20/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.client.extension.toMap
import co.omisego.omisego.client.util.GsonDelegator
import co.omisego.omisego.model.pagination.Paginable.Transaction.SortableFields
import co.omisego.omisego.model.pagination.SortDirection
import co.omisego.omisego.model.params.client.TransactionListParams
import org.amshove.kluent.shouldEqual
import kotlin.test.Test

class TransactionListParamsTest : GsonDelegator() {

    @Test
    fun `ListTransactionParams should be added successfully`() {
        val transactionListParams = TransactionListParams(1,
            10,
            SortableFields.FROM,
            SortDirection.ASCENDING,
            "test",
            address = "address:1234"
        )

        val expected = """
            {"page":1,"per_page":10,"sort_by":"from","sort_dir":"asc","search_term":"test","address":"address:1234"}
        """.trimIndent()

        val actual = gson.toJson(transactionListParams)

        actual.toMap() shouldEqual expected.toMap()
    }
}
