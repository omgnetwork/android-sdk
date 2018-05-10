package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.channel.SocketChannelContract
import com.google.gson.Gson
import okhttp3.WebSocketListener

interface SocketClientContract {
    interface Builder {
        var authenticationToken: String
        var baseURL: String
        var debug: Boolean
        fun build(): SocketClientContract.Core
    }

    interface Core {
        var socketConnectionCallback: SocketConnectionCallback?
        var socketTopicCallback: SocketTopicCallback?
        var socketTransactionEvent: SocketTransactionEvent?

        fun cancel()
        fun hasSentAllMessages(): Boolean
        fun joinChannel(topic: String, event: SocketTransactionEvent)
        fun leaveChannel(topic: String)
        fun setConnectionCallback(callback: SocketConnectionCallback)
        fun setTopicCallback(callback: SocketTopicCallback)
    }

    interface MessageRef {
        var value: String
    }

    interface Channel {
        fun addChannel(topic: String)
        fun removeChannel(topic: String)
        fun retrieveChannels(): Map<String, SocketChannelContract.Channel>
        fun retrieveWebSocketListener(): WebSocketListener
        fun setCallbacks(
            socketConnectionCallback: SocketConnectionCallback?,
            socketTopicCallback: SocketTopicCallback?,
            socketTransactionEvent: SocketTransactionEvent?
        )
    }

    interface PayloadSendParser {
        val gson: Gson
        fun parse(payload: SocketSend): String
    }

    interface Interval {
        fun startInterval()
        fun stopInterval()
    }
}
