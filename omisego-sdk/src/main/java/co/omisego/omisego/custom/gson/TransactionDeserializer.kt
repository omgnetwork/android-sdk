package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.pagination.Paginable
import co.omisego.omisego.model.transaction.Transaction
import co.omisego.omisego.model.transaction.TransactionExchange
import co.omisego.omisego.model.transaction.TransactionSource
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.Date

class TransactionDeserializer : JsonDeserializer<Transaction> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Transaction {
        with(json.asJsonObject) {
            return Transaction(
                get("id").asString,
                context.deserialize(get("status"), Paginable.Transaction.TransactionStatus::class.java),
                context.deserialize(get("from"), TransactionSource::class.java),
                context.deserialize(get("to"), TransactionSource::class.java),
                context.deserialize(get("exchange"), TransactionExchange::class.java),
                context.deserialize(get("metadata"), object : TypeToken<Map<String, Any>>() {}.type),
                context.deserialize(get("encrypted_metadata"), object : TypeToken<Map<String, Any>>() {}.type),
                context.deserialize(get("created_at"), Date::class.java),
                if (get("error_code").isJsonNull || get("error_description").isJsonNull) null else {
                    APIError(ErrorCode.from(get("error_code").asString), get("error_description").asString)
                }
            )
        }
    }
}
