package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.TransactionConsumption
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SocketReceiveDataDeserializer : JsonDeserializer<SocketReceive.SocketData> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SocketReceive.SocketData? {
        val objectType = (json.asJsonObject.get("object") ?: return null).asString

        return when (objectType) {
            "transaction_consumption" -> {
                context.deserialize(json.asJsonObject, TransactionConsumption::class.java)
            }
            else -> {
                SocketReceive.Other(
                    context.deserialize(
                        json.asJsonObject,
                        object : TypeToken<Map<String, Any>>() {}.type
                    )
                )
            }
        }
    }
}
