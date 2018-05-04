package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.websocket.SocketClientContract
import co.omisego.omisego.websocket.enum.SocketEventSend
import co.omisego.omisego.websocket.enum.SocketStatusCode
import okhttp3.WebSocketListener

internal class SocketChannel(
    override val socketDispatcher: SocketChannelContract.Dispatcher,
    override val socketClient: SocketChannelContract.SocketClient
) : SocketClientContract.Channel, SocketChannelContract.Core {
    private val channelList: MutableMap<String, SocketChannelContract.Channel> by lazy { mutableMapOf<String, SocketChannelContract.Channel>() }

    override fun addChannel(topic: String): Map<String, SocketChannelContract.Channel> {
        channelList[topic] = Channel(topic)
        socketClient.send(topic, SocketEventSend.JOIN)
        return channelList
    }

    override fun removeChannel(topic: String): Map<String, SocketChannelContract.Channel> {
        socketClient.send(topic, SocketEventSend.LEAVE)
        channelList.remove(topic)

        if (channelList.isEmpty()) {
            socketClient.closeConnection(SocketStatusCode.NORMAL, "Disconnected successfully")
        }

        return channelList
    }

    override fun retrieveChannels(): Map<String, SocketChannelContract.Channel> {
        return channelList
    }

    override fun retrieveWebSocketCallback(): WebSocketListener {
        return socketDispatcher.retrieveWebSocketListener()
    }

    class Channel(
        override val topic: String
    ) : SocketChannelContract.Channel
}