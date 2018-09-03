package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 22/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.enum.SocketEventSend
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class SocketPendingChannelTest {
    private val socketPendingChannel: SocketPendingChannel by lazy { SocketPendingChannel() }

    @Test
    fun `pending channels should be cached maximum 10 elements properly`() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                socketPendingChannel.pendingChannelsQueue.take()
                socketPendingChannel.pendingChannelsQueue.take()
                socketPendingChannel.pendingChannelsQueue.take()
                socketPendingChannel.pendingChannelsQueue.take()
            }
        }, 3000)

        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic1", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic2", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic3", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic4", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic5", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic6", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic7", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic8", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic9", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic10", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic11", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic12", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic13", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)

        socketPendingChannel.pendingChannelsQueue.size shouldEqualTo 10
        socketPendingChannel.pendingChannelsQueue.last().topic shouldEqualTo "topic13"
    }

    @Test
    fun `execute should call join function for every pending channels`() {
        val join = mock<(topic: String, payload: Map<String, Any>) -> Unit>()
        socketPendingChannel.pendingChannelsQueue += SocketSend("topic", SocketEventSend.JOIN, null, mapOf("a" to 1))
        socketPendingChannel.pendingChannelsQueue += SocketSend("topic2", SocketEventSend.JOIN, null, mapOf())
        socketPendingChannel.pendingChannelsQueue += SocketSend("topic3", SocketEventSend.JOIN, null, mapOf())

        socketPendingChannel.execute(join)

        verify(join).invoke("topic", mapOf("a" to 1))
        verify(join).invoke("topic2", mapOf())
        verify(join).invoke("topic3", mapOf())
    }

    @Test
    fun `The queue should remove a message from the queue if it has`() {
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic", SocketEventSend.JOIN, null, mapOf()))
        socketPendingChannel.pendingChannelsQueue.offer(SocketSend("topic2", SocketEventSend.JOIN, null, mapOf()))

        socketPendingChannel.remove("topic")
        socketPendingChannel.remove("topic3")
        socketPendingChannel.pendingChannelsQueue.size shouldEqualTo 1
    }
}
