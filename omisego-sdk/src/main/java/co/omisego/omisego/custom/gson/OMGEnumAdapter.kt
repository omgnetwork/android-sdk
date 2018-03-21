package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum
import com.google.gson.*
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
class OMGEnumAdapter<T> : JsonDeserializer<T>, JsonSerializer<T> where T : OMGEnum, T : Enum<T> {
    private var values: Map<String, T>? = null

    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): T? {
        val enumConstants = (type as Class<T>).enumConstants
        return (values ?: enumConstants.associateBy { it.value }.also { values = it })[json.asString]
    }

    override fun serialize(src: T, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.value)
    }
}
