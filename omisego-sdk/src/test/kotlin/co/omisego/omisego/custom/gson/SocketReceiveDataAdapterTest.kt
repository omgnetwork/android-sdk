package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 20/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.socket.SocketReceiveData
import co.omisego.omisego.testUtils.GsonProvider
import com.google.gson.Gson
import org.junit.Before
import java.io.File
import kotlin.test.Test

class SocketReceiveDataAdapterTest {
    private val consumeTransactionRequestFile: File by ResourceFile("me.consume_transaction-post.json")
    private lateinit var gson: Gson
    @Before
    fun setUp() {
        gson = GsonProvider.provide()
    }

    @Test
    fun `SocketReceiveDataAdapter should parse TransactionConsumption successfully`() {
        val test = gson.fromJson(consumeTransactionRequestFile.readText(), SocketReceiveData::class.java)
        println(test)
    }
}
