package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import com.google.gson.Gson
import okhttp3.WebSocketListener

interface SocketClientContract {
    /* WebSocket Package */
    interface Builder {
        var authenticationToken: String
        var baseURL: String
        var debug: Boolean
        fun build(): SocketClientContract.Core
    }

    interface Core {
        val socketChannel: Channel
        fun cancel()
        fun hasSentAllMessages(): Boolean
        fun joinChannel(
            topic: SocketTopic,
            payload: Map<String, Any> = mapOf(),
            listener: SocketListenEvent? = null
        )

        fun setConnectionListener(connectionListener: SocketConnectionCallback)
        fun setTopicListener(topicListener: SocketTopicCallback)
        fun leaveChannel(topic: SocketTopic, payload: Map<String, Any>)
    }

    interface PayloadSendParser {
        val gson: Gson
        fun parse(payload: SocketSend): String
    }

    /* Channel Package */
    interface Channel {
        fun addChannel(topic: SocketTopic, payload: Map<String, Any>)
        fun removeChannel(topic: SocketTopic, payload: Map<String, Any>)
        fun retrieveChannels(): Set<SocketTopic>
        fun retrieveWebSocketListener(): WebSocketListener
        fun setSocketConnectionCallback(connectionListener: SocketConnectionCallback?)
        fun setSocketTopicCallback(topicListener: SocketTopicCallback?)
        fun setSocketTransactionCallback(listener: SocketListenEvent?)
    }
}
