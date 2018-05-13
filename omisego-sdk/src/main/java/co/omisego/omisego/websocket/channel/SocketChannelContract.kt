package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.SocketChannelCallback
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketCustomEventCallback
import co.omisego.omisego.websocket.enum.SocketStatusCode
import okhttp3.WebSocketListener
import java.util.Timer

interface SocketChannelContract {
    /* Channel Package */
    interface Channel {
        val socketDispatcher: Dispatcher
        val socketClient: SocketClient
        val socketHeartbeat: SocketInterval
        val socketMessageRef: SocketChannelContract.MessageRef
        val heartbeatTimer: Timer?

        fun createJoinMessage(topic: SocketTopic, payload: Map<String, Any>): SocketSend
        fun createLeaveMessage(topic: SocketTopic, payload: Map<String, Any>): SocketSend
    }

    interface MessageRef {
        var value: String
    }

    /* WebSocket Package */
    interface SocketClient {
        fun send(message: SocketSend): Boolean
        fun closeConnection(status: SocketStatusCode, reason: String)
    }

    /* Interval Package */
    interface SocketInterval {
        var timer: Timer?
        val socketMessageRef: SocketChannelContract.MessageRef

        fun startInterval(task: (SocketSend) -> Unit)
        fun stopInterval()
    }

    /* Dispatcher Package */
    interface Dispatcher {
        fun setSocketConnectionCallback(connectionListener: SocketConnectionCallback?)
        fun setSocketChannelCallback(channelListener: SocketChannelCallback?)
        fun setSocketTransactionCallback(listener: SocketCustomEventCallback?)
        fun retrieveWebSocketListener(): WebSocketListener
    }
}
