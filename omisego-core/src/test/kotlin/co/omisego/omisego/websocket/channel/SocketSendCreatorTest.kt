package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 22/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.enum.SocketEventSend
import org.amshove.kluent.shouldEqual
import org.junit.Test

class SocketSendCreatorTest {
    private val socketSendCreator by lazy { SocketSendCreator(SocketMessageRef(SocketMessageRef.SCHEME_JOIN)) }
    private val topic = SocketTopic("topic")

    @Test
    fun `createJoinMessage should return a SocketSend with JOIN event`() {
        socketSendCreator.createJoinMessage(topic.name, mapOf()) shouldEqual
            SocketSend("topic", SocketEventSend.JOIN, "${SocketMessageRef.SCHEME_JOIN}:1", mapOf())
    }

    @Test
    fun `createLeaveMessage should return a SocketSend with LEAVE event`() {
        socketSendCreator.createLeaveMessage(topic.name, mapOf()) shouldEqual
            SocketSend("topic", SocketEventSend.LEAVE, null, mapOf())
    }
}
