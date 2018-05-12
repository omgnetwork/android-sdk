package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum
import co.omisego.omisego.utils.Either
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class EitherEnumDeserializer<L : OMGEnum, R : OMGEnum> : JsonDeserializer<Either<L, R>> {
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Either<L, R> {
        val types = (typeOfT as ParameterizedType).actualTypeArguments
        val leftEnum = (types[0] as Class<L>)
        val rightEnum = (types[1] as Class<R>)
        val l = leftEnum.enumConstants.firstOrNull { it.value == json.asString }
        val r = rightEnum.enumConstants.firstOrNull { it.value == json.asString }
        return when {
            l != null -> Either.Left(l)
            r != null -> Either.Right(r)
            else -> {
                throw IllegalStateException(
                    "Neither ${leftEnum.simpleName} nor ${rightEnum.simpleName} can be parsed"
                )
            }
        }
    }
}
