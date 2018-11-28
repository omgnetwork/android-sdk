package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.custom.retrofit2.executor.MainThreadExecutor
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.TransactionConsumption
import co.omisego.omisego.utils.Either
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.enum.SocketSystemEvent
import co.omisego.omisego.websocket.listener.SocketConnectionListener
import co.omisego.omisego.websocket.listener.SocketCustomEventListener
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
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
    private val mockSystemEventDispatcher: SocketDispatcherContract.SystemEventDispatcher = mock()
    private val mockCustomEventDispatcher: SocketDispatcherContract.CustomEventDispatcher = mock()
    private val mockSocketConnectionListener: SocketConnectionListener = mock()
    private lateinit var socketDispatcher: SocketDispatcher

    @Before
    fun setup() {
        socketDispatcher = SocketDispatcher(
            mockSystemEventDispatcher,
            mockCustomEventDispatcher,
            mockSocketConnectionListener,
            MainThreadExecutor()
        )
        whenever(mockCustomEventDispatcher.customEventListeners).thenReturn(mock())
    }

    @Test
    fun `addCustomEventListener should be delegated customEventListener correctly`() {
        val mockCustomEventListener: SocketCustomEventListener = mock()
        socketDispatcher.addCustomEventListener(mockCustomEventListener)

        verify(mockCustomEventDispatcher.customEventListeners).add(mockCustomEventListener)
        verifyNoMoreInteractions(mockCustomEventListener)
    }

    @Test
    fun `dispatchOnOpened should invoke the socketConnectionListener's onConnect correctly`() {
        socketDispatcher.dispatchOnOpen(mock())

        verify(mockSocketConnectionListener, times(1)).onConnected()
        verifyNoMoreInteractions(mockSocketConnectionListener)
    }

    @Test
    fun `dispatchOnClosed should invoke the socketConnectionListener's onDisconnected correctly`() {
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
        val mockSocketSystemEventReceive = SocketReceive<TransactionConsumption>(
            "topic",
            systemEvent,
            version = "1",
            success = true
        )

        socketDispatcher.dispatchOnMessage(mockSocketSystemEventReceive)

        verify(mockSystemEventDispatcher).handleEvent(systemEvent.value, mockSocketSystemEventReceive)
        verifyZeroInteractions(mockCustomEventDispatcher)
    }

    @Test
    fun `dispatchOnMessaged should delegate the socketReceive with custom event to the customEventDispatcher and handle correctly`() {
        val customEvent = Either.Right(SocketCustomEvent.TRANSACTION_CONSUMPTION_REQUEST)
        val mockCustomEventReceive = SocketReceive<TransactionConsumption>(
            "topic",
            customEvent,
            version = "1",
            success = true
        )

        socketDispatcher.dispatchOnMessage(mockCustomEventReceive)

        verify(mockCustomEventDispatcher).handleEvent(customEvent.value, mockCustomEventReceive)
        verifyZeroInteractions(mockSystemEventDispatcher)
    }

    @Test
    fun `dispatchOnFailure should invoke socketConnectionListener's onDisconnected correctly`() {
        val mockThrowable: Throwable = mock()
        socketDispatcher.dispatchOnFailure(mockThrowable, any())

        verify(mockSocketConnectionListener, times(1)).onDisconnected(mockThrowable)
        verifyNoMoreInteractions(mockSocketConnectionListener)
    }
}
