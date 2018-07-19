package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 20/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBeInstanceOf
import java.io.File
import kotlin.test.Test

class SocketReceiveDataDeserializerTest : GsonDelegator() {
    private val deserializer by lazy { SocketReceiveDataDeserializer() }
    private val context: JsonDeserializationContext by lazy { mock<JsonDeserializationContext>() }
    private val transactionConsumptionFile: File by ResourceFile("transaction_consumption.json", "object")
    private val transactionRequestFile: File by ResourceFile("transaction_request.json", "object")

    @Test
    fun `deserializer should deserialize the socket receive data with type transaction_consumption correctly`() {
        val jsonElement = JsonParser().parse(transactionConsumptionFile.readText())
        deserializer.deserialize(jsonElement, object : TypeToken<SocketReceive.SocketData>() {}.type, context)

        verify(context, times(1)).deserialize<TransactionConsumption>(jsonElement.asJsonObject, TransactionConsumption::class.java)
    }

    @Test
    fun `deserializer should deserialize the socket receive data with unknown type correctly`() {
        val jsonElement = JsonParser().parse(transactionRequestFile.readText())
        val token = object : TypeToken<Map<String, Any>>() {}.type
        whenever(context.deserialize<Map<String, Any>>(jsonElement, token)).thenReturn(mapOf())

        val response = deserializer.deserialize(jsonElement, token, context)
        response shouldBeInstanceOf SocketReceive.Other::class.java
    }
}
