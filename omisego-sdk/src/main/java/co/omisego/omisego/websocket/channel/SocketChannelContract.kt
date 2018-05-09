package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketMessageRef
import co.omisego.omisego.websocket.SocketTopicCallback
import co.omisego.omisego.websocket.SocketTransactionRequestEvent
import co.omisego.omisego.websocket.enum.SocketStatusCode
import okhttp3.WebSocketListener

interface SocketChannelContract {
    interface Channel {
        val topic: String
    }

    interface Core {
        val socketDispatcher: Dispatcher
        val socketClient: SocketClient
        val socketMessageRef: SocketMessageRef

        fun createJoinMessage(topic: String): SocketSend
        fun createLeaveMessage(topic: String): SocketSend
    }

    interface SocketClient {
        fun send(message: SocketSend): Boolean
        fun closeConnection(status: SocketStatusCode, reason: String)
    }

    interface Dispatcher {
        var socketConnectionCallback: SocketConnectionCallback?
        var socketTopicCallback: SocketTopicCallback?
        var socketTransactionRequestEvent: SocketTransactionRequestEvent?

        fun handleRequestEvents()
        fun handleConsumeEvents()
        fun retrieveWebSocketListener(): WebSocketListener
        fun setCallbacks(
            socketConnectionCallback: SocketConnectionCallback?,
            socketTopicCallback: SocketTopicCallback?,
            socketTransactionRequestEvent: SocketTransactionRequestEvent?
        )
    }
}