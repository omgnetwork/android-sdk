package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 15/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.SocketChannelListener
import co.omisego.omisego.websocket.SocketClientContract
import co.omisego.omisego.websocket.SocketConnectionListener
import co.omisego.omisego.websocket.SocketCustomEventListener
import co.omisego.omisego.websocket.enum.SocketEventSend
import co.omisego.omisego.websocket.enum.SocketStatusCode
import co.omisego.omisego.websocket.interval.SocketHeartbeat
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Test
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class SocketChannelTest {
    private val mockSocketDispatcher: SocketChannelContract.Dispatcher = mock()
    private val mockSocketClient: SocketChannelContract.SocketClient = mock()
    private val mockSocketHeartbeat: SocketClientContract.SocketInterval = mock()
    private val socketTopic = SocketTopic<SocketCustomEventListener.TransactionRequestListener>("topic")
    private lateinit var socketChannel: SocketChannel

    @Before
    fun setup() {
        whenever(mockSocketClient.socketHeartbeat).thenReturn(
            SocketHeartbeat(SocketMessageRef(scheme = SocketMessageRef.SCHEME_HEARTBEAT))
        )
        socketChannel = spy(SocketChannel(mockSocketDispatcher, mockSocketClient))
    }

    @Test
    fun `join should not be send any message when the channel has been joined`() {
        whenever(socketChannel.joined(socketTopic.name)).thenReturn(true)

        socketChannel.join(socketTopic.name, mapOf())

        verifyNoMoreInteractions(mockSocketClient)
    }

    @Test
    fun `join should send join message when the channel has never been joined`() {
        val joinMessage = SocketSend("topic", SocketEventSend.JOIN, "${SocketMessageRef.SCHEME_JOIN}:1", mapOf())
        whenever(socketChannel.joined(socketTopic.name)).thenReturn(false)

        socketChannel.join(socketTopic.name, mapOf())

        verify(mockSocketClient, times(1)).send(joinMessage)
    }

    @Test
    fun `leave should not send any message when the channel has never been joined`() {
        whenever(socketChannel.joined(socketTopic.name)).thenReturn(false)

        socketChannel.leave(socketTopic.name, mapOf())

        verifyZeroInteractions(mockSocketClient)
    }

    @Test
    fun `leave should send leave message when the channel has already been joined`() {
        val leaveMessage = SocketSend("topic", SocketEventSend.LEAVE, null, mapOf())
        whenever(socketChannel.joined(socketTopic.name)).thenReturn(true)

        socketChannel.leave(socketTopic.name, mapOf())

        verify(mockSocketClient, times(1)).send(leaveMessage)
    }

    @Test
    fun `retrieveChannels should return a channel set`() {
        socketChannel.retrieveChannels() shouldEqual mutableSetOf()
    }

    @Test
    fun `joined should return false if the channel set does not contain the specify channel`() {
        socketChannel.joined(socketTopic.name) shouldEqual false
    }

    @Test
    fun `joined should return true if the channel set contain the specify channel`() {
        socketChannel.onJoinedChannel(socketTopic.name)

        socketChannel.joined(socketTopic.name) shouldEqual true
    }

    @Test
    fun `createJoinMessage should return a SocketSend with JOIN event`() {
        socketChannel.createJoinMessage(socketTopic.name, mapOf()) shouldEqual
            SocketSend("topic", SocketEventSend.JOIN, "${SocketMessageRef.SCHEME_JOIN}:1", mapOf())
    }

    @Test
    fun `createLeaveMessage should return a SocketSend with LEAVE event`() {
        socketChannel.createLeaveMessage(socketTopic.name, mapOf()) shouldEqual
            SocketSend("topic", SocketEventSend.LEAVE, null, mapOf())
    }

    @Test
    fun `onJoinedChannel should not start sending a periodic heartbeat event if the channel set is not empty`() {
        // Add first channel
        socketChannel.onJoinedChannel(socketTopic.name)
        whenever(mockSocketClient.socketHeartbeat).thenReturn(mockSocketHeartbeat)

        // Add the existing channel
        socketChannel.onJoinedChannel(socketTopic.name)

        // The heartbeat should not start
        verifyZeroInteractions(mockSocketHeartbeat)
    }

    @Test
    fun `onJoinedChannel should start sending a periodic heartbeat event and add it to the channel set if the channel set is empty`() {
        socketChannel.onJoinedChannel(socketTopic.name)

        Thread.sleep(15)
        verify(mockSocketClient, times(1)).send(
            SocketSend("phoenix", SocketEventSend.HEARTBEAT, "${SocketMessageRef.SCHEME_HEARTBEAT}:1", mapOf())
        )
        socketChannel.retrieveChannels().contains(socketTopic.name) shouldEqualTo true
    }

    @Test
    fun `onLeftChannel should stop an interval and the socket connection if the channel set is empty`() {
        whenever(mockSocketClient.socketHeartbeat).thenReturn(mockSocketHeartbeat)

        // Add the channel first
        socketChannel.onJoinedChannel(socketTopic.name)

        // Then remove it
        socketChannel.onLeftChannel(socketTopic.name)

        socketChannel.retrieveChannels().size shouldEqualTo 0
        verify(mockSocketHeartbeat, times(1)).period = 5000L
        verify(mockSocketHeartbeat, times(1)).stopInterval()
        verify(mockSocketClient, times(1)).closeConnection(SocketStatusCode.NORMAL, "Disconnected successfully")
    }

    @Test
    fun `onLeftChannel should not stop heartbeat interval if the channel set is not empty`() {
        whenever(mockSocketClient.socketHeartbeat).thenReturn(mockSocketHeartbeat)

        socketChannel.onJoinedChannel(socketTopic.name)
        socketChannel.onJoinedChannel("topic2")
        socketChannel.onLeftChannel(socketTopic.name)

        verify(mockSocketHeartbeat, times(0)).stopInterval()
    }

    @Test
    fun `setConnectionListener should bind the connectionListener to the dispatcher correctly`() {
        val mockConnectionListener: SocketConnectionListener = mock()

        socketChannel.setConnectionListener(mockConnectionListener)

        verify(mockSocketDispatcher, times(1)).setSocketConnectionListener(mockConnectionListener)
    }

    @Test
    fun `setChannelListener should bind the channelListener to the dispatcher correctly`() {
        val mockChannelListener: SocketChannelListener = mock()

        socketChannel.setChannelListener(mockChannelListener)

        verify(mockSocketDispatcher, times(1)).setSocketChannelListener(mockChannelListener)
    }

    @Test
    fun `addCustomEventListener should bind the customEventListener to the dispatcher correctly`() {
        val mockCustomEventListener: SocketCustomEventListener = mock()

        socketChannel.addCustomEventListener("", mockCustomEventListener)

        verify(mockSocketDispatcher, times(1)).addCustomEventListener("", mockCustomEventListener)
    }

    @Test
    fun `leaveAll should call leave on every channel in the channel set`() {
        // Join five channels
        for (topic in 1..5) {
            socketChannel.onJoinedChannel(topic.toString())
        }

        socketChannel.leaveAll()

        // Leave five channels
        for (topic in 1..5) {
            verify(mockSocketClient, times(1)).send(SocketSend("$topic", SocketEventSend.LEAVE, null, mapOf()))
        }
    }

    @Test
    fun `join channel should be added to pending channels if joinable is false`() {
        whenever(socketChannel.joinable()).thenReturn(false)

        socketChannel.join("topic", mapOf())

        socketChannel.pendingChannelsQueue.contains(SocketSend("topic", SocketEventSend.JOIN, "join:1", mapOf())) shouldEqualTo true
        verifyZeroInteractions(mockSocketClient)
    }

    @Test
    fun `join channel should not be added to pending channels if joinable is true`() {
        whenever(socketChannel.joinable()).thenReturn(true)

        socketChannel.join("topic", mapOf())

        socketChannel.pendingChannelsQueue.isEmpty() shouldEqualTo true
        verify(mockSocketClient).send(SocketSend("topic", SocketEventSend.JOIN, "join:1", mapOf()))
    }

    @Test
    fun `executePendingJoinChannel should execute join function for every pending channels`() {
        socketChannel.pendingChannelsQueue += SocketSend("topic", SocketEventSend.JOIN, null, mapOf())
        socketChannel.pendingChannelsQueue += SocketSend("topic2", SocketEventSend.JOIN, null, mapOf())
        socketChannel.pendingChannelsQueue += SocketSend("topic3", SocketEventSend.JOIN, null, mapOf())

        socketChannel.executePendingJoinChannel()

        verify(socketChannel).join("topic", mapOf())
        verify(socketChannel).join("topic2", mapOf())
        verify(socketChannel).join("topic3", mapOf())
    }

    @Test
    fun `onJoinedChannel should remove the SocketTopic from the pendingChannels if it has`() {
        socketChannel.pendingChannelsQueue += SocketSend("topic", SocketEventSend.JOIN, null, mapOf())
        socketChannel.pendingChannelsQueue.size shouldEqualTo 1

        socketChannel.onJoinedChannel("topic")

        socketChannel.pendingChannelsQueue.size shouldEqualTo 0
    }

    @Test
    fun `When left the last channel should be clear holding data correctly`() {
        socketChannel.pendingChannelsQueue += SocketSend("topic", SocketEventSend.JOIN, null, mapOf())
        socketChannel.pendingChannelsQueue += SocketSend("topic2", SocketEventSend.JOIN, null, mapOf())

        socketChannel.onLeftChannel("topic")

        socketChannel.pendingChannelsQueue.isEmpty() shouldBe true
        verify(mockSocketDispatcher, times(1)).clearCustomEventListenerMap()
        socketChannel.leavingChannels.get() shouldEqualTo false
        verify(mockSocketClient, times(1)).closeConnection(SocketStatusCode.NORMAL, "Disconnected successfully")
    }

    @Test
    fun `When socket opened, leaving channels flag should be set to false`() {
        socketChannel.onSocketOpened()
        socketChannel.leavingChannels.get() shouldEqualTo false
    }

    @Test
    fun `Pending channels should be cached maximum 10 elements properly`() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                socketChannel.pendingChannelsQueue.take()
                socketChannel.pendingChannelsQueue.take()
                socketChannel.pendingChannelsQueue.take()
                socketChannel.pendingChannelsQueue.take()
            }
        }, 3000)

        socketChannel.pendingChannelsQueue.offer(SocketSend("topic", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic1", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic2", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic3", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic4", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic5", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic6", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic7", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic8", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic9", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic10", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic11", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic12", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic13", SocketEventSend.JOIN, null, mapOf()), 5, TimeUnit.SECONDS)

        socketChannel.pendingChannelsQueue.size shouldEqualTo 10
        socketChannel.pendingChannelsQueue.last().topic shouldEqualTo "topic13"

        println(socketChannel.pendingChannelsQueue)
    }

    @Test
    fun `The client send a join message during leaving the channels, then it should be kept in the queue instead`() {
        socketChannel.join("topic", mapOf()) // the queue should not put this one in the queue.

        verify(mockSocketClient).send(SocketSend("topic", SocketEventSend.JOIN, "join:1", mapOf()))

        socketChannel.leaveAll()

        socketChannel.join("topic1", mapOf()) // the queue should put this one in the queue

        socketChannel.pendingChannelsQueue.size shouldEqualTo 1
        verify(mockSocketClient, times(0)).send(SocketSend("topic1", SocketEventSend.JOIN, "join:1", mapOf()))
    }

    @Test
    fun `The queue should remove a message from the queue when it has already joined`() {
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic", SocketEventSend.JOIN, null, mapOf()))
        socketChannel.pendingChannelsQueue.offer(SocketSend("topic2", SocketEventSend.JOIN, null, mapOf()))

        // The non-existed topic `topic1` has been joined, so the topic `topic` should not be removed from the queue.
        socketChannel.onJoinedChannel("topic1")

        socketChannel.pendingChannelsQueue.size shouldEqualTo 2

        // The existed topic `topic2` has been joined, so the topic `topic2` should be removed from the queue.
        socketChannel.onJoinedChannel("topic2")

        socketChannel.pendingChannelsQueue.size shouldEqualTo 1
        socketChannel.pendingChannelsQueue.take() shouldEqual SocketSend("topic", SocketEventSend.JOIN, null, mapOf())
    }
}
