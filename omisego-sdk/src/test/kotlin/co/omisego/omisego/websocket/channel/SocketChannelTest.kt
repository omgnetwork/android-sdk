package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 15/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.SocketChannelCallback
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketCustomEventCallback
import co.omisego.omisego.websocket.enum.SocketEventSend
import co.omisego.omisego.websocket.enum.SocketStatusCode
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Test

class SocketChannelTest {
    private val mockSocketDispatcher: SocketChannelContract.Dispatcher = mock()
    private val mockSocketClient: SocketChannelContract.SocketClient = mock()
    private val mockSocketHeartbeat: SocketChannelContract.SocketInterval = mock()
    private val socketTopic = SocketTopic<SocketCustomEventCallback.TransactionRequestCallback>("topic")
    private lateinit var socketChannel: SocketChannel

    @Before
    fun setup() {
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
    fun `retrieveWebSocketListener should call socket dispatcher's retrieveWebsocketListener`() {
        socketChannel.retrieveWebSocketListener()

        verify(mockSocketDispatcher, times(1)).retrieveWebSocketListener()
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
        whenever(socketChannel.socketHeartbeat).thenReturn(mockSocketHeartbeat)

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
        whenever(socketChannel.socketHeartbeat).thenReturn(mockSocketHeartbeat)

        // Add the channel first
        socketChannel.onJoinedChannel(socketTopic.name)

        // Then remove it
        socketChannel.onLeftChannel(socketTopic.name)

        socketChannel.retrieveChannels().size shouldEqualTo 0
        verify(mockSocketHeartbeat, times(1)).period = 5000L
        verify(mockSocketHeartbeat, times(1)).stopInterval()
        verify(mockSocketClient, times(1)).closeConnection(SocketStatusCode.NORMAL, "Disconnected successfully")
        verifyNoMoreInteractions(mockSocketClient)
    }

    @Test
    fun `onLeftChannel should not stop heartbeat interval if the channel set is not empty`() {
        whenever(socketChannel.socketHeartbeat).thenReturn(mockSocketHeartbeat)

        socketChannel.onJoinedChannel(socketTopic.name)
        socketChannel.onJoinedChannel("topic2")
        socketChannel.onLeftChannel(socketTopic.name)

        verify(mockSocketHeartbeat, times(0)).stopInterval()
        verifyNoMoreInteractions(mockSocketClient)
    }

    @Test
    fun `setConnectionListener should bind the connectionListener to the dispatcher correctly`() {
        val mockConnectionListener: SocketConnectionCallback = mock()

        socketChannel.setConnectionListener(mockConnectionListener)

        verify(mockSocketDispatcher, times(1)).setSocketConnectionCallback(mockConnectionListener)
    }

    @Test
    fun `setChannelListener should bind the channelListener to the dispatcher correctly`() {
        val mockChannelListener: SocketChannelCallback = mock()

        socketChannel.setChannelListener(mockChannelListener)

        verify(mockSocketDispatcher, times(1)).setSocketChannelCallback(mockChannelListener)
    }

    @Test
    fun `setCustomEventListener should bind the customEventListener to the dispatcher correctly`() {
        val mockCustomEventCallback: SocketCustomEventCallback = mock()

        socketChannel.setCustomEventListener(mockCustomEventCallback)

        verify(mockSocketDispatcher, times(1)).setSocketCustomEventCallback(mockCustomEventCallback)
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
}
