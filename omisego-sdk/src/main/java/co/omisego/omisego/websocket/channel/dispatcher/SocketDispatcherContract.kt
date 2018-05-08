package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import okhttp3.WebSocketListener

interface SocketDispatcherContract {

    interface Core {
        val socketCallback: Callback
        val socketChannel: SocketChannel?
    }

    interface Callback {
        fun getWebSocketListener(): WebSocketListener
    }

    interface SocketChannel {
        fun onLeftChannel(topic: String)
        fun onJoinedChannel(topic: String)
        fun joined(topic: String): Boolean
    }
}