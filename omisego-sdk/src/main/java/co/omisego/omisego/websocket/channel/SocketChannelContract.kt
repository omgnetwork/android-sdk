package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketListenEvent
import co.omisego.omisego.websocket.SocketTopicCallback
import co.omisego.omisego.websocket.enum.SocketStatusCode
import okhttp3.WebSocketListener
import java.util.Timer

interface SocketChannelContract {
    /* Channel Package */
    interface Core {
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
        val socketMessageRef: SocketMessageRef

        fun startInterval(task: (SocketSend) -> Unit)
        fun stopInterval()
    }

    /* Dispatcher Package */
    interface Dispatcher {
        fun setSocketConnectionCallback(connectionListener: SocketConnectionCallback?)
        fun setSocketTopicCallback(topicListener: SocketTopicCallback?)
        fun setSocketTransactionCallback(listener: SocketListenEvent?)
        fun retrieveWebSocketListener(): WebSocketListener
    }
}
