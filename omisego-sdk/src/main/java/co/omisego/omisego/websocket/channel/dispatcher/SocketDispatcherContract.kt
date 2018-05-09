package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.custom.retrofit2.executor.MainThreadExecutor
import okhttp3.WebSocketListener

interface SocketDispatcherContract {

    interface Core {
        val socketDelegator: Delegator
        val socketChannel: SocketChannel?
        val mainThreadExecutor: MainThreadExecutor
    }

    interface Delegator {
        fun getWebSocketListener(): WebSocketListener
    }

    interface SocketChannel {
        fun onLeftChannel(topic: String)
        fun onJoinedChannel(topic: String)
        fun joined(topic: String): Boolean
    }
}