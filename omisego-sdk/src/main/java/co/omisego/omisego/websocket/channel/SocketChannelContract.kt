package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.enum.SocketStatusCode
import okhttp3.WebSocketListener

interface SocketChannelContract {
    interface Channel {
        val topic: String
    }

    interface Core {
        val socketDispatcher: Dispatcher
        val socketClient: SocketClient
    }

    interface SocketClient {
        fun send(message: SocketSend): Boolean
        fun closeConnection(status: SocketStatusCode, reason: String)
    }

    interface Dispatcher {
        fun handleRequestEvents()
        fun handleConsumeEvents()
        fun retrieveWebSocketListener(): WebSocketListener
    }
}