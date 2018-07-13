package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 9/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.websocket.listener.CompositeSocketChannelListener
import co.omisego.omisego.websocket.listener.SocketChannelListener
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class CompositeSocketChannelListenerTest {

    private val compositeSocketChannelListener: CompositeSocketChannelListener by lazy { CompositeSocketChannelListener() }
    private val mockSocketChannelListener1: SocketChannelListener = mock()
    private val mockSocketChannelListener2: SocketChannelListener = mock()

    @Test
    fun `CompositeSocketChannelListener should be able to add different listener object`() {
        compositeSocketChannelListener.add(mockSocketChannelListener1)
        compositeSocketChannelListener.add(mockSocketChannelListener2)

        compositeSocketChannelListener.size shouldEqualTo 2
    }

    @Test
    fun `CompositeSocketChannelListener should not able to add the same listener objects`() {
        compositeSocketChannelListener.add(mockSocketChannelListener1)
        compositeSocketChannelListener.add(mockSocketChannelListener1)

        compositeSocketChannelListener.size shouldEqualTo 1
    }

    @Test
    fun `CompositeSocketChannelListener should be able to invoke all listeners in the set when any event has occurred `() {
        compositeSocketChannelListener.add(mockSocketChannelListener1)
        compositeSocketChannelListener.add(mockSocketChannelListener2)

        compositeSocketChannelListener.onJoinedChannel("topic_1")
        compositeSocketChannelListener.onJoinedChannel("topic_2")

        verify(mockSocketChannelListener1, times(1)).onJoinedChannel("topic_1")
        verify(mockSocketChannelListener1, times(1)).onJoinedChannel("topic_2")
        verify(mockSocketChannelListener2, times(1)).onJoinedChannel("topic_1")
        verify(mockSocketChannelListener2, times(1)).onJoinedChannel("topic_2")

        compositeSocketChannelListener.onLeftChannel("topic_1")
        compositeSocketChannelListener.onLeftChannel("topic_2")

        verify(mockSocketChannelListener1, times(1)).onLeftChannel("topic_1")
        verify(mockSocketChannelListener1, times(1)).onLeftChannel("topic_2")
        verify(mockSocketChannelListener2, times(1)).onLeftChannel("topic_1")
        verify(mockSocketChannelListener2, times(1)).onLeftChannel("topic_2")

        val mockError1: APIError = mock()
        val mockError2: APIError = mock()

        compositeSocketChannelListener.onError(mockError1)
        compositeSocketChannelListener.onError(mockError2)

        verify(mockSocketChannelListener1, times(1)).onError(mockError1)
        verify(mockSocketChannelListener1, times(1)).onError(mockError2)
        verify(mockSocketChannelListener2, times(1)).onError(mockError1)
        verify(mockSocketChannelListener2, times(1)).onError(mockError2)
    }
}
