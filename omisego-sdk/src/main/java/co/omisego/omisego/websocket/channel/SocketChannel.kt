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
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketMessageRef
import co.omisego.omisego.websocket.SocketTopicCallback
import co.omisego.omisego.websocket.SocketTransactionEvent
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcherContract
import co.omisego.omisego.websocket.enum.SocketEventSend
import co.omisego.omisego.websocket.enum.SocketStatusCode
import co.omisego.omisego.websocket.interval.SocketHeartbeat
import co.omisego.omisego.websocket.interval.SocketIntervalContract
import okhttp3.WebSocketListener
import java.util.Timer

internal class SocketChannel(
    override val socketDispatcher: SocketChannelContract.Dispatcher,
    override val socketClient: SocketChannelContract.SocketClient,
    override val socketMessageRef: SocketMessageRef = SocketMessageRef()
) : SocketClientContract.Channel, SocketChannelContract.Core, SocketDispatcherContract.SocketChannel {
    private val channelSet: MutableSet<String> by lazy { mutableSetOf<String>() }
    override val socketHeartbeat: SocketIntervalContract by lazy { SocketHeartbeat(socketMessageRef) }
    override var heartbeatTimer: Timer? = null

    override fun addChannel(topic: String) {
        if (!joined(topic)) {
            socketClient.send(createJoinMessage(topic))
        }
    }

    override fun removeChannel(topic: String) {
        if (joined(topic)) {
            socketClient.send(createLeaveMessage(topic))
        }
    }

    override fun retrieveChannels(): Set<String> = channelSet

    override fun retrieveWebSocketListener(): WebSocketListener = socketDispatcher.retrieveWebSocketListener()

    override fun joined(topic: String) = channelSet.contains(topic)

    override fun createJoinMessage(topic: String): SocketSend =
        SocketSend(topic, SocketEventSend.JOIN, socketMessageRef.value, mapOf())

    override fun createLeaveMessage(topic: String): SocketSend =
        SocketSend(topic, SocketEventSend.LEAVE, socketMessageRef.value, mapOf())

    override fun onJoinedChannel(topic: String) {
        if (channelSet.isEmpty() && topic != SocketHeartbeat.EVENT_NAME) {
            socketHeartbeat.startInterval {
                Log.d("SocketChannel", "Channel List: $channelSet")
                socketClient.send(it)
            }
        }

        // Don't keep heartbeat!
        if (topic != SocketHeartbeat.EVENT_NAME)
            channelSet.add(topic)
    }

    override fun onLeftChannel(topic: String) {
        Log.d("SocketChannel", "onLeftChannel: $topic")
        channelSet.remove(topic)
        if (channelSet.isEmpty()) {
            socketHeartbeat.stopInterval()
            socketClient.closeConnection(SocketStatusCode.NORMAL, "Disconnected successfully")
        }
    }

    override fun setCallbacks(
        socketConnectionCallback: SocketConnectionCallback?,
        socketTopicCallback: SocketTopicCallback?,
        socketTransactionEvent: SocketTransactionEvent?
    ) {
        socketDispatcher.setCallbacks(socketConnectionCallback, socketTopicCallback, socketTransactionEvent)
    }

//    class Channel(
//        override val topic: String
//    ) : SocketChannelContract.Channel
}
