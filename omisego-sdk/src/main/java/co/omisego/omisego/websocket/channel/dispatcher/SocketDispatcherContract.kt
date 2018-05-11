package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.custom.retrofit2.executor.MainThreadExecutor
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.SocketTransactionEvent
import okhttp3.WebSocketListener

interface SocketDispatcherContract {

    interface Core {
        val socketDelegator: Delegator
        val socketChannel: SocketChannel?
        val mainThreadExecutor: MainThreadExecutor

        fun SocketTransactionEvent.RequestEvent.handleTransactionRequestEvent(socketReceive: SocketReceive)
        fun SocketTransactionEvent.ConsumptionEvent.handleTransactionConsumptionEvent(socketReceive: SocketReceive)
    }

    interface Delegator {
        fun getWebSocketListener(): WebSocketListener
    }

    interface SocketChannel {
        fun onLeftChannel(topic: SocketTopic)
        fun onJoinedChannel(topic: SocketTopic)
        fun joined(topic: SocketTopic): Boolean
    }
}
