package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 15/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.constant.HTTPHeaders
import co.omisego.omisego.model.ClientConfiguration
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.channel.SocketChannel
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcher
import co.omisego.omisego.websocket.channel.dispatcher.delegator.SocketDelegator
import co.omisego.omisego.websocket.enum.SocketEventSend
import co.omisego.omisego.websocket.enum.SocketStatusCode
import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionRequestEvent
import co.omisego.omisego.websocket.listener.SocketChannelListener
import co.omisego.omisego.websocket.listener.SocketConnectionListener
import co.omisego.omisego.websocket.listener.SocketCustomEventListener
import co.omisego.omisego.websocket.listener.TransactionRequestListener
import co.omisego.omisego.websocket.strategy.FilterStrategy
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class OMGSocketClientTest {

    private val mockOkHttpClient: OkHttpClient = mock()
    private val mockRequest: Request = mock()
    private val mockWebSocket: WebSocket = mock()
    private val mockWebSocketListener: WebSocketListener = mock()
    private val mockSocketChannel: SocketClientContract.Channel = mock()
    private val mockSocketSendParser: SocketClientContract.PayloadSendParser = mock()
    private val mockSocketDelegator: SocketDelegator = mock()
    private val mockTransactionRequestListener: TransactionRequestListener = mock()
    private val mockLambdaEvent: (SocketEvent<out SocketReceive.SocketData>) -> Unit = mock()

    private lateinit var socketClient: OMGSocketClient

    @Before
    fun setup() {
        socketClient = OMGSocketClient(mockOkHttpClient, mockRequest, mockSocketSendParser, mockSocketDelegator).apply {
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
    fun `hasSentAllMessages should call the web socket client's queue size and check pending channels correctly`() {
        socketClient.socketChannel = mockSocketChannel
        whenever(mockSocketChannel.pending()).thenReturn(true)
        socketClient.hasSentAllMessages()

        verify(mockWebSocket, times(1)).queueSize()
        verify(mockSocketChannel, times(1)).pending()
        verifyNoMoreInteractions(mockWebSocket)
    }

    @Test
    fun `joinChannel should call the socket channel join successfully`() {
        val socketTopic = SocketTopic("topic")
        val payload = mapOf<String, Any>()

        socketClient.socketChannel = mockSocketChannel
        socketClient.joinChannel(socketTopic)

        verify(mockSocketChannel, times(1)).join(socketTopic.name, payload)
        verifyNoMoreInteractions(mockSocketChannel)
    }

    @Test
    fun `addCustomSocketListener with convenient methods should work correctly`() {
        socketClient.socketChannel = mockSocketChannel
        socketClient.addCustomEventListener(mockTransactionRequestListener)
        socketClient.addCustomEventListener(SocketCustomEventListener.forEvent<TransactionConsumptionRequestEvent>(mockLambdaEvent))
        socketClient.addCustomEventListener(SocketCustomEventListener.forTopic(mock { on { socketTopic } doReturn mock<SocketTopic>() }, mockLambdaEvent))
        socketClient.addCustomEventListener(SocketCustomEventListener.forStrategy(FilterStrategy.None(), mockLambdaEvent))
        verify(mockSocketChannel, times(4)).addCustomEventListener(any())
    }

    @Test
    fun `leaveChannel should call the socket channel leave correctly`() {
        val socketTopic = SocketTopic("topic")
        val payload = mapOf<String, Any>()

        socketClient.socketChannel = mockSocketChannel
        socketClient.leaveChannel(socketTopic, payload)

        verify(mockSocketChannel, times(1)).leave(socketTopic.name, payload)
        verifyNoMoreInteractions(mockSocketChannel)
    }

    @Test
    fun `setIntervalPeriod should set a new period to the socket channel correctly`() {
        socketClient.socketChannel = mockSocketChannel
        socketClient.setIntervalPeriod(10_000)

        verify(mockSocketChannel, times(1)).period = 10_000
    }

    @Test
    fun `addConnectionListener should delegate the listener to the socket channel correctly`() {
        socketClient.socketChannel = mockSocketChannel
        val socketConnectionListener: SocketConnectionListener = mock()

        socketClient.addConnectionListener(socketConnectionListener)

        verify(mockSocketChannel, times(1)).addConnectionListener(socketConnectionListener)
    }

    @Test
    fun `addChannelListener should delegate the listener to the socket channel correctly`() {
        socketClient.socketChannel = mockSocketChannel
        val socketChannelListener: SocketChannelListener = mock()

        socketClient.addChannelListener(socketChannelListener)

        verify(mockSocketChannel, times(1)).addChannelListener(socketChannelListener)
    }

    @Test
    fun `send should initialize the new web socket client if there's no the existing web socket available yet`() {
        socketClient.wsClient = null
        socketClient.socketChannel = mockSocketChannel
        val socketSend = SocketSend("topic", SocketEventSend.JOIN, "1", mapOf())

        whenever(mockSocketDelegator.webSocketListener).thenReturn(mockWebSocketListener)
        whenever(mockOkHttpClient.newWebSocket(mockRequest, mockWebSocketListener)).thenReturn(mockWebSocket)
        whenever(mockSocketSendParser.parse(socketSend)).thenReturn("Hi, web socket")

        socketClient.send(socketSend)

        verify(mockOkHttpClient, times(1)).newWebSocket(mockRequest, mockWebSocketListener)
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
    fun `build should create an instance of the socket client successfully given both valid authenticationToken and baseURL`() {
        val config = ClientConfiguration(
            "ws://localhost:4000/",
            "api1234",
            "at1234"
        )

        val socketClient = OMGSocketClient.Builder {
            clientConfiguration = config
            debug = false
        }.build() as OMGSocketClient

        socketClient shouldNotBe null
        socketClient.okHttpClient shouldNotBe null
        socketClient.request shouldNotBe null
        socketClient.socketChannel shouldNotBe null
        socketClient.socketSendParser shouldNotBe null

        // Validate dependencies flow wired up between OMGSocketClient <-- SocketChannel --> SocketDispatcher
        val channel = socketClient.socketChannel as SocketChannel
        channel.socketDispatcher shouldNotBe null
        channel.socketClient shouldNotBe null

        // Validate dependencies flow wired up between SocketChannel <-- SocketDispatcher --> SocketDelegator, SystemEventDispatcher, CustomEventDispatcher
        val dispatcher = channel.socketDispatcher as SocketDispatcher
        dispatcher.systemEventDispatcher shouldNotBe null
        dispatcher.customEventDispatcher shouldNotBe null
    }

    @Test
    fun `setAuthenticationHeader should call leaveAll in socketChannel and build a new request with new authentication header`() {
        whenever(mockRequest.url()).thenReturn(HttpUrl.Builder().host("localhost").scheme("http").build())
        socketClient.socketChannel = mockSocketChannel

        socketClient.setAuthenticationHeader("apiKey", "authToken")

        verify(mockRequest, times(1)).url()
        verify(mockSocketChannel, times(1)).leaveAll()

        // Expecting a base64 of "apiKey:authToken"
        socketClient.request.header(HTTPHeaders.AUTHORIZATION) shouldEqual "OMGClient YXBpS2V5OmF1dGhUb2tlbg=="
    }

    @Test
    fun `OMGSocketClient should throws an IllegalStateException if the clientConfiguration is not set`() {
        val error = { OMGSocketClient.Builder { }.build() }
        error shouldThrow IllegalStateException::class withMessage Exceptions.MSG_NULL_CLIENT_CONFIGURATION
    }
}
