package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializer
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
internal class OMGEnumAdapter<T: OMGEnum> : JsonDeserializer<T>, JsonSerializer<T> {
    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): T? {
        val enumConstants = (type as Class<T>).enumConstants
        return enumConstants.firstOrNull { it.value == json.asString }
    }

    override fun serialize(src: T, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.value)
    }
}
