package co.omisego.omisego.custom.gson

import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.SocketCustomEventListener
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Test

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 24/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class SocketTopicDeserializerTest : GsonDelegator() {

    data class TestData(
        val topic: SocketTopic<SocketCustomEventListener.TransactionRequestListener>
    )

    @Test
    fun `SocketTopicDeserializer should be able to deserialize socketTopic successfully`() {
        val json = """{"topic": "transaction_request:1234"}"""
        val testData = gson.fromJson<TestData>(json, TestData::class.java)
        testData.topic shouldBeInstanceOf SocketTopic::class.java
    }
}
