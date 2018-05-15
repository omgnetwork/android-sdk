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
        val leftEnumClass = (types[0] as Class<L>)
        val rightEnumClass = (types[1] as Class<R>)
        val leftEnum = leftEnumClass.findFirstNonNullOMGEnum(json.asString)
        val rightEnum = rightEnumClass.findFirstNonNullOMGEnum(json.asString)
        return when {
            leftEnum != null -> Either.Left(leftEnum)
            rightEnum != null -> Either.Right(rightEnum)
            else -> {
                throw IllegalStateException(
                    "Neither ${leftEnumClass.simpleName} nor ${rightEnumClass.simpleName} can be parsed"
                )
            }
        }
    }

    private fun <T : OMGEnum> Class<T>.findFirstNonNullOMGEnum(
        predicate: String,
        backupPredicate: String = "other"
    ): T? {
        return this.enumConstants.firstOrNull { it.value == predicate }
            ?: this.enumConstants.firstOrNull { it.value == backupPredicate }
            ?: return null
    }
}
