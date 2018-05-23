package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 20/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.utils.GsonProvider
import com.google.gson.Gson
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import java.io.File
import kotlin.test.Test

class SocketReceiveDataDeserializerTest {
    private val transactionConsumptionFile: File by ResourceFile("transaction_consumption.json")
    private val malformedTransactionConsumptionFile: File by ResourceFile("malformed_transaction_consumption.json")
    private lateinit var gson: Gson
    @Before
    fun setUp() {
        gson = GsonProvider.create()
    }

    @Test
    fun `SocketReceiveDataDeserializer should parse TransactionConsumption successfully`() {
        val test = gson.fromJson(transactionConsumptionFile.readText(), SocketReceive::class.java)
        test.data shouldBeInstanceOf TransactionConsumption::class.java
    }

    @Test
    fun `SocketReceiveDataDeserializer should parse malformed TransactionConsumption to Map successfully`() {
        val test = gson.fromJson(malformedTransactionConsumptionFile.readText(), SocketReceive::class.java)
        test.data shouldBeInstanceOf SocketReceive.Other::class.java
    }
}
