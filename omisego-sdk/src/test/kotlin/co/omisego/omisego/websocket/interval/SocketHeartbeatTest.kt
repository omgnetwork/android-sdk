package co.omisego.omisego.websocket.interval

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.channel.SocketChannelContract
import com.nhaarman.mockito_kotlin.spy
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBeInRange
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
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
        var countFinishedThread = 0
        val allThreads = mutableListOf<Thread>()
        val task = mock<(SocketSend) -> Unit>()
        for (i in 1..100) {
            val t = thread(start = false) {
                socketHeartbeat.startInterval(task)
            }
            allThreads.add(t)
        }

        // Wait all threads finish their worked.
        allThreads.forEach {
            it.start()
            it.join()
            if (!it.isAlive && !it.isInterrupted) countFinishedThread++
        }

        /**
         * Ensure the following situation will not happen.
         *
         * timer?.cancel() <-- Thread B (execute before thread A)
         * timer = Timer()
         * timer = timer?.schedule(whatever) <-- Thread A (thread A will throw IllegalStateException, so task() won't be invoked)
         *
         * Because of that, this expression will verify that all tasks should be invoked.
         */
        Mockito.mockingDetails(task).invocations.size shouldBeInRange (countFinishedThread - 3)..100 // approximately
    }
}
