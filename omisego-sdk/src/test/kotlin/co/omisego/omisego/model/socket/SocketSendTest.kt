package co.omisego.omisego.model.socket

import co.omisego.omisego.websocket.enum.SocketEventSend
import org.amshove.kluent.shouldEqual
import org.junit.Test

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/6/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class SocketSendTest {

    @Test
    fun `SocketSend with the same topic and same event should be equal`() {
        val socketSend1 = SocketSend("topic", SocketEventSend.JOIN, null, mapOf())
        val socketSend2 = SocketSend("topic", SocketEventSend.JOIN, "test:1", mapOf("1" to 1))

        socketSend1 shouldEqual socketSend2
    }
}
