package co.omisego.omisego.websocket.interval

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 23/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.enum.SocketEventSend
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.timeout
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import java.util.Timer

class SocketReconnectTest {

    private val socketReconnect: SocketReconnect = spy(SocketReconnect())
    private val mockSocketSend: SocketSend = mock()
    private val mockJoin: (SocketSend) -> Unit = mock()

    @Test
    fun `startInterval should call join channel correctly if the network is available`() {
        whenever(socketReconnect.isInternetAvailable()).thenReturn(true)
        socketReconnect.reconnectChannels.add(mockSocketSend)

        socketReconnect.startInterval(mockJoin)
        verify(mockJoin, timeout(1000).times(1)).invoke(mockSocketSend)
    }

    @Test
    fun `startInterval should not call join channel if the network is not available`() {
        whenever(socketReconnect.isInternetAvailable()).thenReturn(false)
        socketReconnect.reconnectChannels.add(mockSocketSend)

        socketReconnect.startInterval(mockJoin)
        verify(mockJoin, timeout(1000).times(0)).invoke(mockSocketSend)
    }

    @Test
    fun `stopReconnectIfDone should call stopInterval if the having all channel set in the reconnectChannelSet`() {
        socketReconnect.timer = Timer()
        socketReconnect.reconnectChannels.add(SocketSend("topic1", SocketEventSend.JOIN, null, mapOf()))
        socketReconnect.reconnectChannels.add(SocketSend("topic2", SocketEventSend.JOIN, null, mapOf()))

        socketReconnect.stopReconnectIfDone(setOf("topic1", "topic2"))
        verify(socketReconnect, times(1)).stopInterval()
        socketReconnect.timer shouldBe null
    }

    @Test
    fun `stopReconnectIfDone should not call stopInterval if not having all channel set in the reconnectChannelSet`() {
        socketReconnect.timer = Timer()
        socketReconnect.reconnectChannels.add(SocketSend("topic2", SocketEventSend.JOIN, null, mapOf()))

        socketReconnect.stopReconnectIfDone(setOf("topic1", "topic2"))
        verify(socketReconnect, times(0)).stopInterval()
        socketReconnect.timer shouldNotBe null
    }

    @Test
    fun `The channel should be removed from reconnectChannels if sending an existed topic`() {
        val element = SocketSend("topic", SocketEventSend.JOIN, null, mapOf())
        socketReconnect.reconnectChannels.add(element)
        socketReconnect.reconnectChannels.size shouldEqualTo 1

        socketReconnect.remove("topic")
        socketReconnect.reconnectChannels.size shouldEqualTo 0
    }

    @Test
    fun `The channel should not be removed from reconnectChannels if the topic doesn't exist`() {
        val element = SocketSend("topic", SocketEventSend.JOIN, null, mapOf())
        socketReconnect.reconnectChannels.add(element)
        socketReconnect.reconnectChannels.size shouldEqualTo 1

        socketReconnect.remove("topic2")
        socketReconnect.reconnectChannels.size shouldEqualTo 1
    }
}
