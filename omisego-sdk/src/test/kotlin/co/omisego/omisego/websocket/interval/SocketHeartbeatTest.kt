package co.omisego.omisego.websocket.interval

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.channel.SocketChannelContract
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.junit.Before
import org.junit.Test
import kotlin.concurrent.thread

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class SocketHeartbeatTest {

    private val mockMessageRef: SocketChannelContract.MessageRef = mock()
    private lateinit var socketHeartbeat: SocketHeartbeat

    @Before
    fun setup() {
        socketHeartbeat = spy(SocketHeartbeat(mockMessageRef).apply { period = 5000L })
    }

    @Test
    fun `startInterval should be a thread-safe function`() {
        val allThreads = mutableListOf<Thread>()
        val task = mock<(SocketSend) -> Unit>()
        for (i in 1..1_000) {
            val t = thread {
                socketHeartbeat.startInterval(task)
            }
            allThreads.add(t)
        }

        // Wait all threads finish their worked.
        for (i in 0..999) {
            allThreads[i].join()
        }

        // Wait task to be called for short period.
        Thread.sleep(50)

        verify(task, times(1_000)).invoke(any())
    }
}
