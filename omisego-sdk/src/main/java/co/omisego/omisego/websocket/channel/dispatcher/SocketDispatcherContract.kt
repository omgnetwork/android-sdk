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
import co.omisego.omisego.websocket.SocketChannelCallback
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketCustomEventCallback
import co.omisego.omisego.websocket.enum.SocketBasicEvent
import co.omisego.omisego.websocket.enum.SocketFeaturedEvent
import okhttp3.WebSocketListener

interface SocketDispatcherContract {

    /* Dispatcher Package */
    interface Dispatcher {
        val socketDelegator: Delegator
        val systemEventDispatcher: SystemEventDispatcher
        val customEventDispatcher: CustomEventDispatcher
        val socketChannel: SocketChannel?
        val mainThreadExecutor: MainThreadExecutor
        var socketConnectionListener: SocketConnectionCallback?
    }

    interface SystemEventDispatcher {
        var socketConnectionCallback: SocketConnectionCallback?
        var socketChannelCallback: SocketChannelCallback?
        var socketReceive: SocketReceive?
        var socketChannel: SocketChannel?
        fun handleEvent(basicEvent: SocketBasicEvent)
    }

    interface CustomEventDispatcher {
        var socketCustomEventCallback: SocketCustomEventCallback?
        var socketChannelCallback: SocketChannelCallback?
        var socketReceive: SocketReceive?
        fun handleEvent(featuredEvent: SocketFeaturedEvent)
        fun SocketCustomEventCallback.TransactionRequestCallback.handleTransactionRequestEvent(
            socketReceive: SocketReceive,
            featuredEvent: SocketFeaturedEvent
        )
        fun SocketCustomEventCallback.TransactionConsumptionCallback.handleTransactionConsumptionEvent(
            socketReceive: SocketReceive,
            featuredEvent: SocketFeaturedEvent
        )
    }

    /* Delegator Package */
    interface Delegator {
        fun getWebSocketListener(): WebSocketListener
    }

    /* Channel Package */
    interface SocketChannel {
        fun onLeftChannel(topic: SocketTopic)
        fun onJoinedChannel(topic: SocketTopic)
        fun joined(topic: SocketTopic): Boolean
    }
}
