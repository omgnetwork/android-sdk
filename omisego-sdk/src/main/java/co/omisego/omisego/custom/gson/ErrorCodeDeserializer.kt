package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.ErrorCode
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class ErrorCodeDeserializer : JsonDeserializer<ErrorCode> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ErrorCode {
        return ErrorCode.from(json.asString)
    }
}
