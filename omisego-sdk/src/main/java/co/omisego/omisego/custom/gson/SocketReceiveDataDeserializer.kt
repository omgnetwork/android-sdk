package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceiveData
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SocketReceiveDataDeserializer : JsonDeserializer<SocketReceiveData> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SocketReceiveData? {
        val objectType = (json.asJsonObject.get("object") ?: return null).asString

        return when (objectType) {
            "transaction_consumption" -> {
                SocketReceiveData.SocketConsumeTransaction(
                    context.deserialize(
                        json.asJsonObject,
                        object : TypeToken<TransactionConsumption>() {}.type
                    )
                )
            }
            else -> {
                SocketReceiveData.Other(
                    context.deserialize(
                        json.asJsonObject,
                        object : TypeToken<Map<String, Any>>() {}.type
                    )
                )
            }
        }
    }
}
