package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 28/11/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.filterable.Comparator
import co.omisego.omisego.model.filterable.Filter
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class NullSerializationTest {

    data class NullData(val value: Any? = null)

    @Test
    fun `test null value should not be serialized`() {
        GsonProvider.create().toJson(NullData()) shouldEqualTo "{}"
    }

    @Test
    fun `test null value of filter object should be serialized`() {
        GsonProvider.create().toJson(Filter("some_field", Comparator.NullComparator.NULL(), null)) shouldEqualTo """
        {
          "field": "some_field",
          "comparator": "eq",
          "value": null
        }
        """.trimIndent()
    }
}
