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
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.utils.Either
import co.omisego.omisego.websocket.channel.SocketMessageRef
import co.omisego.omisego.websocket.enum.SocketSystemEvent
import co.omisego.omisego.websocket.listener.internal.CompositeSocketChannelListener
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import org.junit.Before
import org.junit.Test

class SystemEventDispatcherTest {
    private val dataHeartbeat: SocketReceive<TransactionConsumption> = SocketReceive(
        "phoenix",
        event = Either.Left(SocketSystemEvent.REPLY),
        data = null,
        version = "1",
        ref = "${SocketMessageRef.SCHEME_HEARTBEAT}:1",
        success = true
    )

    private val dataPhxReply: SocketReceive<TransactionConsumption> = SocketReceive(
        "topic",
        event = Either.Left(SocketSystemEvent.REPLY),
        data = null,
        version = "1",
        ref = "${SocketMessageRef.SCHEME_JOIN}:1",
        success = true
    )

    private val dataPhxClose: SocketReceive<TransactionConsumption> = SocketReceive(
        "topic",
        event = Either.Left(SocketSystemEvent.CLOSE),
        data = null,
        version = "1",
        ref = "${SocketMessageRef.SCHEME_JOIN}:1",
        success = true
    )

    private val dataPhxError: SocketReceive<TransactionConsumption> = SocketReceive(
        "topic",
        event = Either.Left(SocketSystemEvent.ERROR),
        data = null,
        version = "1",
        success = false
    )

    private val mockSocketChannelListener: CompositeSocketChannelListener = mock()
    private lateinit var systemEventDispatcher: SystemEventDispatcher

    @Before
    fun setup() {
        systemEventDispatcher = SystemEventDispatcher(mockSocketChannelListener)
    }

    @Test
    fun `handleEvent should not invoke any listener if it is a heartbeat event`() {
        systemEventDispatcher.handleEvent(SocketSystemEvent.REPLY, dataHeartbeat)

        verifyNoMoreInteractions(mockSocketChannelListener)
    }

    @Test
    fun `handleEvent PHX_CLOSE should invoke onLeftChannel function`() {
        systemEventDispatcher.handleEvent(SocketSystemEvent.CLOSE, dataPhxClose)

        verify(mockSocketChannelListener, times(1)).onLeftChannel(dataPhxClose.topic)
        verifyNoMoreInteractions(mockSocketChannelListener)
    }

    @Test
    fun `handleEvent PHX_REPLY should invoke onJoinedChannel function`() {
        systemEventDispatcher.handleEvent(SocketSystemEvent.REPLY, dataPhxReply)
        verify(mockSocketChannelListener, times(1)).onJoinedChannel(dataPhxReply.topic)
    }

    @Test
    fun `handleEvent PHX_ERROR should invoke onError function with proper error code and description`() {
        systemEventDispatcher.handleEvent(SocketSystemEvent.ERROR, dataPhxError)

        verify(mockSocketChannelListener, times(1)).onError(
            APIError(
                ErrorCode.SDK_SOCKET_ERROR,
                "Something goes wrong while connecting to the channel"
            )
        )
    }
}
