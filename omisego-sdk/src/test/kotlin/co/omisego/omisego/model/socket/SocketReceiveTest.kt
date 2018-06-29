package co.omisego.omisego.model.socket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/6/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.utils.Either
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class SocketReceiveTest : GsonDelegator() {
    private val socketReceiveFile by ResourceFile("socket_receive.json", "object")
    private val socketReceive by lazy { gson.fromJson(socketReceiveFile.readText(), SocketReceive::class.java) }

    @Test
    fun `socket_receive should be parsed correctly`() {
        with(socketReceive) {
            version shouldEqualTo "1"
            success shouldEqualTo true
            topic shouldEqualTo "a_topic"
            ref shouldEqual "1"
            event shouldEqual Either.Right(SocketCustomEvent.OTHER)
            error shouldEqual null
            data shouldBeInstanceOf TransactionConsumption::class.java
        }
    }
}
