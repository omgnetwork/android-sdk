package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.custom.retrofit2.executor.MainThreadExecutor
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.utils.Either
import co.omisego.omisego.websocket.SocketChannelCallback
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketCustomEventCallback
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.enum.SocketSystemEvent
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.SocketException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class SocketDispatcherTest {
    private val mockDelegator: SocketDispatcherContract.Delegator = mock()
    private val mockSystemEventDispatcher: SocketDispatcherContract.SystemEventDispatcher = mock()
    private val mockCustomEventDispatcher: SocketDispatcherContract.CustomEventDispatcher = mock()
    private val mockSocketConnectionListener: SocketConnectionCallback = mock()
    private val mockSocketChannel: SocketDispatcherContract.SocketChannel = mock()
    private val mockMainThreadExecutor: MainThreadExecutor = mock()
    private lateinit var socketDispatcher: SocketDispatcher

    @Before
    fun setup() {
        socketDispatcher = SocketDispatcher(mockDelegator, mockSystemEventDispatcher, mockCustomEventDispatcher)
    }

    @Test
    fun `setSocketChannel should also delegate to systemEventDispatcher`() {
        socketDispatcher.socketChannel = mockSocketChannel

        verify(mockSystemEventDispatcher, times(1)).socketChannel = mockSocketChannel
        verifyNoMoreInteractions(mockCustomEventDispatcher, mockSystemEventDispatcher)
    }

    @Test
    fun `setSocketConnectionCallback should be delegated socketConnectionCallback correctly`() {
        socketDispatcher.setSocketConnectionCallback(mockSocketConnectionListener)

        socketDispatcher.socketConnectionListener shouldEqual mockSocketConnectionListener
        verify(mockSystemEventDispatcher).socketConnectionCallback = mockSocketConnectionListener
        verifyNoMoreInteractions(mockSystemEventDispatcher, mockCustomEventDispatcher)
    }

    @Test
    fun `setSocketChannelCallback should be delegated the socketChannelCallback correctly`() {
        val mockChannelCallback: SocketChannelCallback = mock()
        socketDispatcher.setSocketChannelCallback(mockChannelCallback)

        verify(mockSystemEventDispatcher).socketChannelCallback = mockChannelCallback
        verify(mockCustomEventDispatcher).socketChannelCallback = mockChannelCallback
        verifyNoMoreInteractions(mockSystemEventDispatcher, mockCustomEventDispatcher)
    }

    @Test
    fun `setSocketCustomEventCallback should be delegated customEventListener correctly`() {
        val mockCustomEventCallback: SocketCustomEventCallback = mock()
        socketDispatcher.setSocketCustomEventCallback(mockCustomEventCallback)

        verify(mockCustomEventDispatcher).socketCustomEventCallback = mockCustomEventCallback
        verifyNoMoreInteractions(mockCustomEventCallback)
    }

    @Test
    fun `retrieveWebSocketListener should be retrieved from the socketDelegator`() {
        socketDispatcher.retrieveWebSocketListener()

        verify(mockDelegator, times(1)).retrievesWebSocketListener()
        verifyNoMoreInteractions(mockDelegator)
    }

    @Test
    fun `dispatchOnOpened should invoke the socketConnectionListener's onConnect correctly`() {
        socketDispatcher.socketConnectionListener = mockSocketConnectionListener
        socketDispatcher.dispatchOnOpened(mock())

        verify(mockSocketConnectionListener, times(1)).onConnected()
        verifyNoMoreInteractions(mockSocketConnectionListener)
    }

    @Test
    fun `dispatchOnClosed should invoke the socketConnectionListener's onDisconnected correctly`() {
        socketDispatcher.socketConnectionListener = mockSocketConnectionListener
        socketDispatcher.dispatchOnClosed(1000, "")
        verify(mockSocketConnectionListener).onDisconnected(null)

        val code = 1001
        val reason = "A server is going down"
        val socketExceptionArgCaptor: ArgumentCaptor<SocketException> = ArgumentCaptor.forClass(SocketException::class.java)

        socketDispatcher.dispatchOnClosed(code, reason)

        verify(mockSocketConnectionListener, times(2)).onDisconnected(socketExceptionArgCaptor.capture())
        socketExceptionArgCaptor.value.message shouldEqual "$code $reason"
    }

    @Test
    fun `dispatchOnMessaged should delegate the socketReceive with system event to the systemEventDispatcher and handle correctly`() {
        val systemEvent = Either.Left(SocketSystemEvent.REPLY)
        val mockSocketSystemEventReceive = SocketReceive(
            "topic",
            systemEvent,
            version = "1",
            success = true
        )

        socketDispatcher.dispatchOnMessage(mockSocketSystemEventReceive)

        verify(mockSystemEventDispatcher).socketReceive = mockSocketSystemEventReceive
        verify(mockSystemEventDispatcher).handleEvent(systemEvent.value)
        verifyZeroInteractions(mockCustomEventDispatcher)
    }

    @Test
    fun `dispatchOnMessaged should delegate the socketReceive with custom event to the customEventDispatcher and handle correctly`() {
        val customEvent = Either.Right(SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST)
        val mockCustomEventReceive = SocketReceive(
            "topic",
            customEvent,
            version = "1",
            success = true
        )

        socketDispatcher.dispatchOnMessage(mockCustomEventReceive)

        verify(mockCustomEventDispatcher).socketReceive = mockCustomEventReceive
        verify(mockCustomEventDispatcher).handleEvent(customEvent.value)
        verifyZeroInteractions(mockSystemEventDispatcher)
    }

    @Test
    fun `dispatchOnFailure should invoke socketConnectionListener's onDisconnected correctly`() {
        val mockThrowable: Throwable = mock()
        socketDispatcher.socketConnectionListener = mockSocketConnectionListener
        socketDispatcher.dispatchOnFailure(mockThrowable, any())

        verify(mockSocketConnectionListener, times(1)).onDisconnected(mockThrowable)
        verifyNoMoreInteractions(mockSocketConnectionListener)
    }

    @Test
    fun `talksTo should assign socketChannel correctly`() {
        with(socketDispatcher) {
            this talksTo mockSocketChannel
            this.socketChannel shouldEqual mockSocketChannel
        }
    }
}
