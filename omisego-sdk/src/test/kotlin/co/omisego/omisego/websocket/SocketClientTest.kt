package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 15/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.channel.SocketChannel
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcher
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketDelegator
import co.omisego.omisego.websocket.enum.SocketEventSend
import co.omisego.omisego.websocket.enum.SocketStatusCode
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.amshove.kluent.shouldNotThrowTheException
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.Before
import org.junit.Test

class SocketClientTest {

    private val mockOkHttpClient: OkHttpClient = mock()
    private val mockRequest: Request = mock()
    private val mockWebSocket: WebSocket = mock()
    private val mockSocketChannel: SocketClientContract.Channel = mock()
    private val mockCustomEventListener: SocketCustomEventCallback = mock()
    private val mockSocketSendParser: SocketClientContract.PayloadSendParser = mock()

    private lateinit var socketClient: SocketClient

    @Before
    fun setup() {
        socketClient = SocketClient(mockOkHttpClient, mockRequest, mockSocketSendParser).apply {
            wsClient = mockWebSocket
        }
    }

    @Test
    fun `Cancel should invoke the web socket client to cancel correctly`() {
        socketClient.cancel()

        verify(mockWebSocket, times(1)).cancel()
        verifyNoMoreInteractions(mockWebSocket)
    }

    @Test
    fun `hasSentAllMessages should call the web socket client's queue size correctly`() {
        socketClient.hasSentAllMessages()

        verify(mockWebSocket, times(1)).queueSize()
        verifyNoMoreInteractions(mockWebSocket)
    }

    @Test
    fun `joinChannel should call the socket channel join and setCustomEventListener correctly`() {
        val socketTopic = SocketTopic("topic")
        val payload = mapOf<String, Any>()

        socketClient.socketChannel = mockSocketChannel
        socketClient.joinChannel(socketTopic, payload, mockCustomEventListener)

        verify(mockSocketChannel, times(1)).join(socketTopic, payload)
        verify(mockSocketChannel, times(1)).setCustomEventListener(mockCustomEventListener)
        verifyNoMoreInteractions(mockSocketChannel)
    }

    @Test
    fun `leaveChannel should call the socket channel leave correctly`() {
        val socketTopic = SocketTopic("topic")
        val payload = mapOf<String, Any>()

        socketClient.socketChannel = mockSocketChannel
        socketClient.leaveChannel(socketTopic, payload)

        verify(mockSocketChannel, times(1)).leave(socketTopic, payload)
        verifyNoMoreInteractions(mockSocketChannel)
    }

    @Test
    fun `setIntervalPeriod should set a new period to the socket channel correctly`() {
        socketClient.socketChannel = mockSocketChannel
        socketClient.setIntervalPeriod(10_000)

        verify(mockSocketChannel, times(1)).period = 10_000
    }

    @Test
    fun `setConnectionListener should delegate the callback to the socket channel correctly`() {
        socketClient.socketChannel = mockSocketChannel
        val socketConnectionCallback: SocketConnectionCallback = mock()

        socketClient.setConnectionListener(socketConnectionCallback)

        verify(mockSocketChannel, times(1)).setConnectionListener(socketConnectionCallback)
    }

    @Test
    fun `setChannelListener should delegate the callback to the socket channel correctly`() {
        socketClient.socketChannel = mockSocketChannel
        val socketChannelListener: SocketChannelCallback = mock()

        socketClient.setChannelListener(socketChannelListener)

        verify(mockSocketChannel, times(1)).setChannelListener(socketChannelListener)
    }

    @Test
    fun `send should initialize the new web socket client if there's no the existing web socket available yet`() {
        socketClient.wsClient = null
        socketClient.socketChannel = mockSocketChannel
        val socketSend = SocketSend("topic", SocketEventSend.JOIN, "1", mapOf())

        whenever(mockOkHttpClient.newWebSocket(mockRequest, mockSocketChannel.retrieveWebSocketListener())).thenReturn(mockWebSocket)
        whenever(mockSocketSendParser.parse(socketSend)).thenReturn("Hi, web socket")

        socketClient.send(socketSend)

        verify(mockSocketChannel, times(2)).retrieveWebSocketListener()
        verify(mockOkHttpClient, times(1)).newWebSocket(mockRequest, mockSocketChannel.retrieveWebSocketListener())
        verify(mockSocketSendParser, times(1)).parse(socketSend)
        verify(mockWebSocket, times(1)).send("Hi, web socket")
    }

    @Test
    fun `send should not initialize the new web socket client if there's already has the existing web socket`() {
        socketClient.socketChannel = mockSocketChannel
        val socketSend = SocketSend("topic", SocketEventSend.JOIN, "1", mapOf())
        whenever(mockSocketSendParser.parse(socketSend)).thenReturn("Hi, web socket")

        socketClient.send(socketSend)

        verify(mockSocketSendParser, times(1)).parse(socketSend)
        verify(mockWebSocket, times(1)).send("Hi, web socket")
        verifyZeroInteractions(mockSocketChannel, mockOkHttpClient)
    }

    @Test
    fun `closeConnection should call the web socket client to close correctly`() {
        val reason = "I don't have any reason to close it, but I do want :p"
        socketClient.closeConnection(SocketStatusCode.NORMAL, reason)

        verify(mockWebSocket, times(1)).close(SocketStatusCode.NORMAL.code, reason)
        socketClient.wsClient shouldEqual null
    }

    @Test
    fun `authenticationToken should throw an IllegalStateException when assigning it the empty value`() {
        val error = {
            SocketClient.Builder {
                authenticationToken = ""
            }
        }

        error shouldThrow IllegalStateException::class withMessage Exceptions.MSG_EMPTY_AUTH_TOKEN
    }

    @Test
    fun `baseURL should throw an IllegalStateException when assigning it the empty value`() {
        val error = {
            SocketClient.Builder {
                baseURL = ""
            }
        }

        error shouldThrow IllegalStateException::class withMessage Exceptions.MSG_EMPTY_BASE_URL
    }

    @Test
    fun `authenticationToken should not be thrown an exception when assigning non-empty value`() {
        val error = {
            SocketClient.Builder {
                authenticationToken = "a1234"
            }
        }

        error shouldNotThrowTheException IllegalStateException::class
    }

    @Test
    fun `baseURL should not be thrown an exception when assigning non-empty value`() {
        val error = {
            SocketClient.Builder {
                baseURL = "ws://localhost:4000/"
            }
        }

        error shouldNotThrowTheException IllegalStateException::class
    }

    @Test
    fun `build should create an instance of the socket client successfully given both valid authenticationToken and baseURL`() {
        val socketClient = SocketClient.Builder {
            baseURL = "ws://localhost:4000/"
            authenticationToken = "a1234"
        }.build() as SocketClient

        socketClient shouldNotBe null
        socketClient.okHttpClient shouldNotBe null
        socketClient.request shouldNotBe null
        socketClient.socketChannel shouldNotBe null
        socketClient.socketSendParser shouldNotBe null

        // Validate dependencies flow wired up between SocketClient <-- SocketChannel --> SocketDispatcher
        val channel = socketClient.socketChannel as SocketChannel
        channel.socketDispatcher shouldNotBe null
        channel.socketClient shouldNotBe null

        // Validate dependencies flow wired up between SocketChannel <-- SocketDispatcher --> SocketDelegator, SystemEventDispatcher, CustomEventDispatcher
        val dispatcher = channel.socketDispatcher as SocketDispatcher
        dispatcher.socketChannel shouldNotBe null
        dispatcher.socketDelegator shouldNotBe null
        dispatcher.systemEventDispatcher shouldNotBe null
        dispatcher.customEventDispatcher shouldNotBe null

        // Validate dependencies flow wired up between SocketDispatcher <-- SocketDelegator --> SocketResponseParser
        val delegator = dispatcher.socketDelegator as SocketDelegator
        delegator.socketDispatcher shouldNotBe null
        delegator.socketResponseParser shouldNotBe null
    }
}
