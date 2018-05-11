package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.util.Log
import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
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
    private val channelSet: MutableSet<SocketTopic> by lazy { mutableSetOf<SocketTopic>() }
    override val socketHeartbeat: SocketIntervalContract by lazy { SocketHeartbeat(socketMessageRef) }
    override var heartbeatTimer: Timer? = null

    override fun addChannel(topic: SocketTopic, payload: Map<String, Any>) {
        if (!joined(topic)) {
            socketClient.send(createJoinMessage(topic, payload))
        }
    }

    override fun removeChannel(topic: SocketTopic, payload: Map<String, Any>) {
        if (joined(topic)) {
            socketClient.send(createLeaveMessage(topic, payload))
        }
    }

    override fun retrieveChannels(): Set<SocketTopic> = channelSet

    override fun retrieveWebSocketListener(): WebSocketListener = socketDispatcher.retrieveWebSocketListener()

    override fun joined(topic: SocketTopic) = channelSet.contains(topic)

    override fun createJoinMessage(topic: SocketTopic, payload: Map<String, Any>): SocketSend =
        SocketSend(topic.name, SocketEventSend.JOIN, socketMessageRef.value, payload)

    override fun createLeaveMessage(topic: SocketTopic, payload: Map<String, Any>): SocketSend =
        SocketSend(topic.name, SocketEventSend.LEAVE, socketMessageRef.value, payload)

    override fun onJoinedChannel(topic: SocketTopic) {
        with(topic) {
            runIfEmptyChannel {
                socketHeartbeat.startInterval {
                    Log.d("SocketChannel", "Channel List: $channelSet")
                    socketClient.send(it)
                }
            }
            channelSet.add(topic)
        }
    }

    override fun onLeftChannel(topic: SocketTopic) {
        with(topic) {
            Log.d("SocketChannel", "onLeftChannel: $this")
            channelSet.remove(this)
            runIfEmptyChannel {
                socketHeartbeat.stopInterval()
                socketClient.closeConnection(SocketStatusCode.NORMAL, "Disconnected successfully")
            }
        }
    }

    override fun setCallbacks(
        socketConnectionCallback: SocketConnectionCallback?,
        socketTopicCallback: SocketTopicCallback?,
        socketTransactionEvent: SocketTransactionEvent?
    ) {
        socketDispatcher.setCallbacks(socketConnectionCallback, socketTopicCallback, socketTransactionEvent)
    }

    internal inline fun runIfEmptyChannel(doSomething: () -> Unit) {
        if (channelSet.isEmpty()) {
            doSomething()
        }
    }
}
