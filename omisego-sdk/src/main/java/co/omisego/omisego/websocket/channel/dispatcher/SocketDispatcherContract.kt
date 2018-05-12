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
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketListenEvent
import co.omisego.omisego.websocket.SocketTopicCallback
import co.omisego.omisego.websocket.enum.SocketBasicEvent
import co.omisego.omisego.websocket.enum.SocketFeaturedEvent
import okhttp3.WebSocketListener

interface SocketDispatcherContract {

    interface Core {
        val socketDelegator: Delegator
        val systemEventDispatcher: SystemEventDispatcher
        val sendableEventDispatcher: SendableEventDispatcher
        val socketChannel: SocketChannel?
        val mainThreadExecutor: MainThreadExecutor
        var socketConnectionListener: SocketConnectionCallback?
    }

    interface Delegator {
        fun getWebSocketListener(): WebSocketListener
    }

    interface SocketChannel {
        fun onLeftChannel(topic: SocketTopic)
        fun onJoinedChannel(topic: SocketTopic)
        fun joined(topic: SocketTopic): Boolean
    }

    interface SystemEventDispatcher {
        var socketConnectionCallback: SocketConnectionCallback?
        var socketTopicCallback: SocketTopicCallback?
        var socketReceive: SocketReceive?
        var socketChannel: SocketChannel?
        fun handleEvent(basicEvent: SocketBasicEvent)
    }

    interface SendableEventDispatcher {
        var socketListenEvent: SocketListenEvent?
        var socketTopicCallback: SocketTopicCallback?
        var socketReceive: SocketReceive?
        fun handleEvent(featuredEvent: SocketFeaturedEvent)
        fun SocketListenEvent.TransactionRequestEvent.handleTransactionRequestEvent(
            socketReceive: SocketReceive,
            featuredEvent: SocketFeaturedEvent
        )
        fun SocketListenEvent.TransactionConsumptionEvent.handleTransactionConsumptionEvent(
            socketReceive: SocketReceive,
            featuredEvent: SocketFeaturedEvent
        )
    }
}
