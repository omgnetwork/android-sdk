package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.util.Log
import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.SocketClientContract
import co.omisego.omisego.websocket.SocketMessageRef
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcherContract
import co.omisego.omisego.websocket.enum.SocketEventSend
import co.omisego.omisego.websocket.enum.SocketStatusCode
import okhttp3.WebSocketListener

internal class SocketChannel(
    override val socketDispatcher: SocketChannelContract.Dispatcher,
    override val socketClient: SocketChannelContract.SocketClient,
    override val socketMessageRef: SocketMessageRef = SocketMessageRef()
) : SocketClientContract.Channel, SocketChannelContract.Core, SocketDispatcherContract.SocketChannel {
    private val channelList: MutableMap<String, SocketChannelContract.Channel> by lazy { mutableMapOf<String, SocketChannelContract.Channel>() }

    override fun addChannel(topic: String) {
        socketClient.send(createJoinMessage(topic))
    }

    override fun removeChannel(topic: String) {
        socketClient.send(createLeaveMessage(topic))
    }

    override fun retrieveChannels(): Map<String, SocketChannelContract.Channel> = channelList

    override fun retrieveWebSocketCallback(): WebSocketListener = socketDispatcher.retrieveWebSocketListener()

    override fun joined(topic: String) = channelList.containsKey(topic)

    override fun createJoinMessage(topic: String): SocketSend =
        SocketSend(topic, SocketEventSend.JOIN, socketMessageRef.value, mapOf())

    override fun createLeaveMessage(topic: String): SocketSend =
        SocketSend(topic, SocketEventSend.LEAVE, socketMessageRef.value, mapOf())

    override fun onJoinedChannel(topic: String) {
        Log.d("SocketChannel", "onJoinedChannel: $topic")
        channelList[topic] = Channel(topic)
    }

    override fun onLeftChannel(topic: String) {
        Log.d("SocketChannel", "onLeftChannel: $topic")
        channelList.remove(topic)
        if (channelList.isEmpty()) {
            socketClient.closeConnection(SocketStatusCode.NORMAL, "Disconnected successfully")
        }
    }

    class Channel(
        override val topic: String
    ) : SocketChannelContract.Channel
}
