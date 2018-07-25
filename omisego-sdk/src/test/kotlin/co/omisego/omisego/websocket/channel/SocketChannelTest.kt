package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 15/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.enum.SocketEventSend
import co.omisego.omisego.websocket.enum.SocketStatusCode
import co.omisego.omisego.websocket.interval.SocketHeartbeat
import co.omisego.omisego.websocket.interval.SocketReconnect
import co.omisego.omisego.websocket.listener.SocketCustomEventListener
import co.omisego.omisego.websocket.listener.internal.CompositeSocketChannelListener
import co.omisego.omisego.websocket.listener.internal.CompositeSocketConnectionListener
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.timeout
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
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

class SocketChannelTest {
    private val mockSocketDispatcher: SocketChannelContract.Dispatcher = mock()
    private val mockSocketClient: SocketChannelContract.SocketClient = mock()
    private val mockSocketHeartbeat: SocketHeartbeat = mock()
    private val mockSocketPendingChannel: SocketPendingChannel = mock()
    private val mockSocketReconnect: SocketReconnect = mock()
    private val mockCompositeChannelListener: CompositeSocketChannelListener = mock()
    private val mockCompositeConnectionListener: CompositeSocketConnectionListener = mock()
    private val mockSocketSendCreator: SocketSendCreator = mock()
    private val joinMessage = SocketSend("topic", SocketEventSend.JOIN, "${SocketMessageRef.SCHEME_JOIN}:1", mapOf())
    private val leaveMessage = SocketSend("topic", SocketEventSend.LEAVE, null, mapOf())
    private lateinit var socketChannel: SocketChannel

    @Before
    fun setup() {
        whenever(mockSocketSendCreator.createJoinMessage(joinMessage.topic, joinMessage.data)).thenReturn(joinMessage)
        whenever(mockSocketSendCreator.createLeaveMessage(leaveMessage.topic, leaveMessage.data)).thenReturn(leaveMessage)
        whenever(mockSocketPendingChannel.pendingChannelsQueue).thenReturn(
            ArrayBlockingQueue<SocketSend>(10, true)
        )
        whenever(mockSocketClient.socketHeartbeat).thenReturn(
            SocketHeartbeat(SocketMessageRef(scheme = SocketMessageRef.SCHEME_HEARTBEAT))
        )
        socketChannel = spy(SocketChannel(
            mockSocketDispatcher,
            mockSocketClient,
            mockCompositeConnectionListener,
            mockCompositeChannelListener,
            socketPendingChannel = mockSocketPendingChannel,
            socketReconnect = mockSocketReconnect,
            socketSendCreator = mockSocketSendCreator
        ))
    }

    @Test
    fun `join should not be send any message when the channel has been joined`() {
        whenever(socketChannel.joined(joinMessage.topic)).thenReturn(true)

        socketChannel.join(joinMessage.topic, mapOf())

        verifyNoMoreInteractions(mockSocketClient)
    }

    @Test
    fun `join should send join message when the channel has never been joined`() {
        whenever(socketChannel.leavingAllChannels).thenReturn(AtomicBoolean(false))
        whenever(socketChannel.joined(joinMessage.topic)).thenReturn(false)

        socketChannel.join(joinMessage.topic, mapOf())

        verify(mockSocketClient, times(1)).send(joinMessage)
    }

    @Test
    fun `leave should not send any message when the channel has never been joined`() {
        whenever(socketChannel.joined(joinMessage.topic)).thenReturn(false)

        socketChannel.leave(joinMessage.topic, mapOf())

        verifyZeroInteractions(mockSocketClient)
    }

    @Test
    fun `leave should send leave message when the channel has already been joined`() {

        whenever(socketChannel.joined(joinMessage.topic)).thenReturn(true)

        socketChannel.leave(joinMessage.topic, mapOf())

        verify(mockSocketClient, times(1)).send(leaveMessage)
    }

    @Test
    fun `retrieveChannels should return a channel set`() {
        socketChannel.retrieveChannels() shouldEqual mutableSetOf()
    }

    @Test
    fun `joined should return false if the channel set does not contain the specify channel`() {
        socketChannel.joined(joinMessage.topic) shouldEqual false
    }

    @Test
    fun `joined should return true if the channel set contain the specify channel`() {
        socketChannel.onJoinedChannel(joinMessage.topic)

        socketChannel.joined(joinMessage.topic) shouldEqual true
    }

    @Test
    fun `onJoinedChannel should not start sending a periodic heartbeat event if the channel set is not empty`() {
        // Add first channel
        socketChannel.channelSet.add("topic")
        whenever(mockSocketClient.socketHeartbeat).thenReturn(mockSocketHeartbeat)

        // Add the existing channel
        socketChannel.onJoinedChannel(joinMessage.topic)

        // The heartbeat should not start
        verifyZeroInteractions(mockSocketHeartbeat)
    }

    @Test
    fun `onJoinedChannel should start sending a periodic heartbeat event and add it to the channel set if the channel set is empty`() {
        socketChannel.onJoinedChannel(joinMessage.topic)

        verify(mockSocketClient, timeout(1_000).times(1)).send(
            SocketSend("phoenix", SocketEventSend.HEARTBEAT, "${SocketMessageRef.SCHEME_HEARTBEAT}:1", mapOf())
        )
        socketChannel.retrieveChannels().contains(joinMessage.topic) shouldEqualTo true
    }

    @Test
    fun `onLeftChannel should stop an interval and the socket connection if the channel set is empty`() {
        whenever(mockSocketClient.socketHeartbeat).thenReturn(mockSocketHeartbeat)

        // Add the channel first
        socketChannel.onJoinedChannel(joinMessage.topic)

        // Then remove it
        socketChannel.onLeftChannel(joinMessage.topic)

        socketChannel.retrieveChannels().size shouldEqualTo 0
        verify(mockSocketHeartbeat, times(1)).period = 5000L
        verify(mockSocketHeartbeat, times(1)).stopInterval()
        verify(mockSocketClient, times(1)).closeConnection(SocketStatusCode.NORMAL, "Disconnected successfully")
    }

    @Test
    fun `onLeftChannel should not stop heartbeat interval if the channel set is not empty`() {
        whenever(mockSocketClient.socketHeartbeat).thenReturn(mockSocketHeartbeat)

        socketChannel.onJoinedChannel(joinMessage.topic)
        socketChannel.onJoinedChannel("topic2")
        socketChannel.onLeftChannel(joinMessage.topic)

        verify(mockSocketHeartbeat, never()).stopInterval()
    }

    @Test
    fun `onLeftChannel should remove the channel from the socketReconnect`() {
        socketChannel.onLeftChannel("topic")
        verify(mockSocketReconnect, times(1)).remove("topic")
    }

    @Test
    fun `When left the last channel should be clear holding data correctly`() {
        socketChannel.onJoinedChannel("topic")
        socketChannel.onLeftChannel("topic")
        socketChannel.onJoinedChannel("topic2")
        socketChannel.onJoinedChannel("topic3")
        socketChannel.onLeftChannel("topic2")
        verify(socketChannel, times(1)).disconnect(SocketStatusCode.NORMAL, "Disconnected successfully")
    }

    @Test
    fun `disconnect with socket status code NORMAL should clear all custom event listener`() {
        socketChannel.disconnect(SocketStatusCode.NORMAL, "test")

        mockSocketClient.socketHeartbeat.period shouldEqualTo 5000L
        mockSocketClient.socketHeartbeat.timer shouldBe null
        mockSocketPendingChannel.pendingChannelsQueue.size shouldBe 0
        socketChannel.leavingAllChannels.get() shouldEqual false
        verify(mockSocketDispatcher).clearCustomEventListeners()
        verify(mockSocketClient).closeConnection(SocketStatusCode.NORMAL, "test")
    }

    @Test
    fun `disconnect with socket status code CONNECTION_FAILURE should not clear custom event listener`() {
        socketChannel.disconnect(SocketStatusCode.CONNECTION_FAILURE, "test")

        mockSocketClient.socketHeartbeat.period shouldEqualTo 5000L
        mockSocketClient.socketHeartbeat.timer shouldBe null
        mockSocketPendingChannel.pendingChannelsQueue.size shouldBe 0
        socketChannel.leavingAllChannels.get() shouldEqual false
        verify(mockSocketDispatcher, never()).clearCustomEventListeners()
        verify(mockSocketClient).closeConnection(SocketStatusCode.CONNECTION_FAILURE, "test")
    }

    @Test
    fun `addCustomEventListener should bind the customEventListener to the dispatcher correctly`() {
        val mockCustomEventListener: SocketCustomEventListener = mock()

        socketChannel.addCustomEventListener(mockCustomEventListener)

        verify(mockSocketDispatcher, times(1)).addCustomEventListener(mockCustomEventListener)
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
            verify(socketChannel, times(1)).leave(topic.toString(), mapOf())
        }
    }

    @Test
    fun `join channel should be added to pending channels if leavingAllChannels is true`() {
        whenever(socketChannel.leavingAllChannels).thenReturn(AtomicBoolean(true))

        socketChannel.join(joinMessage.topic, joinMessage.data)

        verify(mockSocketPendingChannel, times(1)).add(joinMessage, period = 5000L)
    }

    @Test
    fun `join channel should not be added to pending channels if leavingAllChannels is false`() {
        whenever(socketChannel.leavingAllChannels).thenReturn(AtomicBoolean(false))

        socketChannel.join(joinMessage.topic, joinMessage.data)

        verify(mockSocketPendingChannel, never()).add(joinMessage, period = 5000L)
        verify(mockSocketClient).send(joinMessage)
    }

    @Test
    fun `onJoinedChannel should remove the SocketTopic from the pendingChannels if it has`() {
        socketChannel.onJoinedChannel("topic")
        verify(mockSocketPendingChannel, times(1)).remove("topic")
        verifyNoMoreInteractions(mockSocketPendingChannel)
    }

    @Test
    fun `onJoinedChannel should call startSocketHeartbeat if it is the first channel to join`() {
        socketChannel.onJoinedChannel("topic")
        verify(socketChannel, times(1)).startHeartbeatWhenBegin()
    }

    @Test
    fun `onJoinedChannel should call stopReconnectIfDone correctly`() {
        socketChannel.onJoinedChannel("topic")
        verify(mockSocketReconnect, times(1)).stopReconnectIfDone(setOf("topic"))
    }

    @Test
    fun `when socket is opened, leaving channels flag should be set to false`() {
        socketChannel.onConnected()
        socketChannel.leavingAllChannels.get() shouldEqualTo false
    }

    @Test
    fun `when socket is opened, socket pending channel should execute join channel`() {
        mockSocketPendingChannel.pendingChannelsQueue.add(SocketSend("topic1", data = mapOf(), event = SocketEventSend.JOIN, ref = null))

        socketChannel.onConnected()

        verify(socketChannel, times(1)).join("topic1", mapOf())
    }

    @Test
    fun `The queue should remove a message from the queue when it has already joined`() {
        socketChannel.onJoinedChannel("topic")

        verify(mockSocketPendingChannel, times(1)).remove("topic")
        verifyNoMoreInteractions(mockSocketPendingChannel)
    }
}
