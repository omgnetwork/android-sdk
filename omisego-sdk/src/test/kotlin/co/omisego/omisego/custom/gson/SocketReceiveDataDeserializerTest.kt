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
import org.amshove.kluent.shouldBeInstanceOf
import java.io.File
import kotlin.test.Test

class SocketReceiveDataDeserializerTest : GsonDelegator() {
    private val transactionConsumptionFile: File by ResourceFile("transaction_consumption.json")
    private val malformedTransactionConsumptionFile: File by ResourceFile("malformed_transaction_consumption.json")

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
