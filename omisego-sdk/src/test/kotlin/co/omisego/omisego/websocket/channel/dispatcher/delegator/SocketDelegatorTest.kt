package co.omisego.omisego.websocket.channel.dispatcher.delegator

import co.omisego.omisego.model.socket.SocketReceive
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import okhttp3.Response
import okhttp3.WebSocket
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.junit.Before
import org.junit.Test

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 13/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class SocketDelegatorTest {
    private val mockSocketDispatcher: SocketDelegatorContract.Dispatcher = mock()
    private val mockSocketResponseParser: SocketDelegatorContract.PayloadReceiveParser = mock()
    private val mockWebSocket: WebSocket = mock()

    private lateinit var socketDelegator: SocketDelegator

    @Before
    fun setup() {
        socketDelegator = SocketDelegator(mockSocketResponseParser).apply {
            socketDispatcher = mockSocketDispatcher
        }
    }

    @Test
    fun `dispatchOnOpened is invoked correctly when the onOpen is called`() {
        val response = mock<Response>()
        socketDelegator.onOpen(mockWebSocket, response)
        verify(mockSocketDispatcher, times(1)).dispatchOnOpened(response)
    }

    @Test
    fun `dispatchOnFailure is invoked correctly when the onFailure is called`() {
        val response = mock<Response>()
        val throwable = mock<Throwable>()
        socketDelegator.onFailure(mockWebSocket, throwable, response)
        verify(mockSocketDispatcher, times(1)).dispatchOnFailure(throwable, response)
    }

    @Test
    fun `dispatchOnMessage is invoked correctly when the onMessage is called`() {
        val text = "¯\\_(ツ)_/¯"
        val socketReceive = mock<SocketReceive>()
        whenever(mockSocketResponseParser.parse(text)).thenReturn(socketReceive)

        socketDelegator.onMessage(mockWebSocket, text)
        verify(mockSocketDispatcher, times(1)).dispatchOnMessage(socketReceive)
    }

    @Test
    fun `dispatchOnClosed is invoked correctly when the onClosed is called`() {
        val code = 1000
        val reason = "¯\\_(ツ)_/¯"
        socketDelegator.onClosed(mockWebSocket, code, reason)
        verify(mockSocketDispatcher, times(1)).dispatchOnClosed(code, reason)
    }

    @Test
    fun `retrievesWebSocketListener should return the SocketDelegator instance`() {
        socketDelegator.retrievesWebSocketListener() shouldBe socketDelegator
    }
}
