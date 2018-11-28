package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.filterable.Comparator
import co.omisego.omisego.model.filterable.Filter
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

@Suppress("UNCHECKED_CAST")
internal class FilterSerializer : TypeAdapter<Filter>() {
    override fun write(out: JsonWriter?, filter: Filter) {
        out?.serializeNulls = true
        out?.beginObject()
        out?.name("field")
        out?.value(filter.field)
        out?.name("comparator")
        out?.value(filter.comparator.value)
        out?.name("value")
        val propVal = filter.value
        when (propVal) {
            is String -> out?.value(propVal)
            is Boolean -> out?.value(propVal)
            is Number -> out?.value(propVal)
            else -> out?.nullValue()
        }
        out?.endObject()
        out?.serializeNulls = false
    }

    override fun read(reader: JsonReader?): Filter {
        // Unused
        return Filter(
            "", Comparator.StringComparator.EQUAL(), null
        )
    }
}
