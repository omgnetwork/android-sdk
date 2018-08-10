package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 9/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.websocket.listener.SocketConnectionListener
import co.omisego.omisego.websocket.listener.internal.CompositeSocketConnectionListener
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class CompositeSocketConnectionListenerTest {
    private val compositeSocketConnectionListener: CompositeSocketConnectionListener by lazy { CompositeSocketConnectionListener() }
    private val mockSocketConnectionListener1: SocketConnectionListener = mock()
    private val mockSocketConnectionListener2: SocketConnectionListener = mock()

    @Test
    fun `CompositeSocketConnectionListener should be able to add different listener object`() {
        compositeSocketConnectionListener.add(mockSocketConnectionListener1)
        compositeSocketConnectionListener.add(mockSocketConnectionListener2)

        compositeSocketConnectionListener.size shouldEqualTo 2
    }

    @Test
    fun `CompositeSocketConnectionListener should not able to add the same listener objects`() {
        compositeSocketConnectionListener.add(mockSocketConnectionListener1)
        compositeSocketConnectionListener.add(mockSocketConnectionListener1)

        compositeSocketConnectionListener.size shouldEqualTo 1
    }

    @Test
    fun `CompositeSocketConnectionListener should be able to invoke all listeners in the set when any event has occurred `() {
        compositeSocketConnectionListener.add(mockSocketConnectionListener1)
        compositeSocketConnectionListener.add(mockSocketConnectionListener2)

        compositeSocketConnectionListener.onConnected()
        val mockThrowable = mock<Throwable>()
        compositeSocketConnectionListener.onDisconnected(mockThrowable)

        verify(mockSocketConnectionListener1, times(1)).onConnected()
        verify(mockSocketConnectionListener1, times(1)).onDisconnected(mockThrowable)
        verify(mockSocketConnectionListener2, times(1)).onConnected()
        verify(mockSocketConnectionListener2, times(1)).onDisconnected(mockThrowable)
    }
}
