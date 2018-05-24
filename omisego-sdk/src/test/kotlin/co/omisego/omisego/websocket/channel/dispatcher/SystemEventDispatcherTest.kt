package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 15/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.utils.Either
import co.omisego.omisego.websocket.SocketChannelCallback
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.channel.SocketMessageRef
import co.omisego.omisego.websocket.enum.SocketSystemEvent
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.junit.Before
import org.junit.Test

class SystemEventDispatcherTest {
    private val dataHeartbeat: SocketReceive = SocketReceive(
        "phoenix",
        event = Either.Left(SocketSystemEvent.REPLY),
        data = null,
        version = "1",
        ref = "${SocketMessageRef.SCHEME_HEARTBEAT}:1",
        success = true
    )

    private val dataPhxReply: SocketReceive = SocketReceive(
        "topic",
        event = Either.Left(SocketSystemEvent.REPLY),
        data = null,
        version = "1",
        ref = "${SocketMessageRef.SCHEME_JOIN}:1",
        success = true
    )

    private val dataPhxClose: SocketReceive = SocketReceive(
        "topic",
        event = Either.Left(SocketSystemEvent.CLOSE),
        data = null,
        version = "1",
        ref = "${SocketMessageRef.SCHEME_JOIN}:1",
        success = true
    )

    private val dataPhxError: SocketReceive = SocketReceive(
        "topic",
        event = Either.Left(SocketSystemEvent.ERROR),
        data = null,
        version = "1",
        success = false
    )

    private val socketConnectionCallback: SocketConnectionCallback = mock()
    private val socketChannelCallback: SocketChannelCallback = mock()
    private val socketChannel: SocketDispatcherContract.SocketChannel = mock()

    private lateinit var systemEventDispatcher: SystemEventDispatcher

    @Before
    fun setup() {
        systemEventDispatcher = SystemEventDispatcher().apply {
            socketConnectionCallback = this@SystemEventDispatcherTest.socketConnectionCallback
            socketChannelCallback = this@SystemEventDispatcherTest.socketChannelCallback
            socketChannel = this@SystemEventDispatcherTest.socketChannel
        }
    }

    @Test
    fun `handleEvent should not invoke any callback if it is a heartbeat event`() {
        systemEventDispatcher.socketReceive = dataHeartbeat
        systemEventDispatcher.handleEvent(SocketSystemEvent.REPLY)

        verifyNoMoreInteractions(socketChannel, socketChannelCallback, socketConnectionCallback)
    }

    @Test
    fun `handleEvent PHX_CLOSE should invoke onLeftChannel function`() {
        systemEventDispatcher.socketReceive = dataPhxClose
        systemEventDispatcher.handleEvent(SocketSystemEvent.CLOSE)

        verify(socketChannel, times(1)).onLeftChannel(dataPhxClose.topic)
        verify(socketChannelCallback, times(1)).onLeftChannel(dataPhxClose.topic)
        verifyNoMoreInteractions(socketChannel, socketChannelCallback)
    }

    @Test
    fun `handleEvent PHX_REPLY should invoke onJoinedChannel function`() {
        systemEventDispatcher.socketReceive = dataPhxReply
        systemEventDispatcher.handleEvent(SocketSystemEvent.REPLY)

        verify(socketChannel, times(1)).onJoinedChannel(dataPhxReply.topic)
        verify(socketChannelCallback, times(1)).onJoinedChannel(dataPhxReply.topic)
    }

    @Test
    fun `handleEvent PHX_REPLY should not invoke any callback if the channel topic has already joined`() {
        whenever(socketChannel.joined(any())).thenReturn(true)
        systemEventDispatcher.socketReceive = dataPhxReply
        systemEventDispatcher.handleEvent(SocketSystemEvent.REPLY)

        verify(socketChannel, times(0)).onJoinedChannel(any())
        verifyNoMoreInteractions(socketChannelCallback)
    }

    @Test
    fun `handleEvent PHX_ERROR should invoke onError function with proper error code and description`() {
        systemEventDispatcher.socketReceive = dataPhxError
        systemEventDispatcher.handleEvent(SocketSystemEvent.ERROR)

        verify(socketChannelCallback, times(1)).onError(
            APIError(
                ErrorCode.SDK_SOCKET_ERROR,
                "Something goes wrong while connecting to the channel"
            )
        )
    }
}
