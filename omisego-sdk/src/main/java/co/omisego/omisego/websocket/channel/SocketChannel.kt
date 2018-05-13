package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.SocketChannelCallback
import co.omisego.omisego.websocket.SocketClientContract
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.SocketCustomEventCallback
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcherContract
import co.omisego.omisego.websocket.channel.interval.SocketHeartbeat
import co.omisego.omisego.websocket.enum.SocketEventSend
import co.omisego.omisego.websocket.enum.SocketStatusCode
import okhttp3.WebSocketListener

internal class SocketChannel(
    override val socketDispatcher: SocketChannelContract.Dispatcher,
    override val socketClient: SocketChannelContract.SocketClient,
    override val socketMessageRef: SocketChannelContract.MessageRef = SocketMessageRef()
) : SocketClientContract.Channel, SocketChannelContract.Channel, SocketDispatcherContract.SocketChannel {
    private val channelSet: MutableSet<SocketTopic> by lazy { mutableSetOf<SocketTopic>() }
    override var period: Long = 5000
    override val socketHeartbeat: SocketChannelContract.SocketInterval by lazy { SocketHeartbeat(socketMessageRef) }

    override fun join(topic: SocketTopic, payload: Map<String, Any>) {
        if (!joined(topic)) {
            socketClient.send(createJoinMessage(topic, payload))
        }
    }

    override fun leave(topic: SocketTopic, payload: Map<String, Any>) {
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
                    socketClient.send(it)
                }
            }
            channelSet.add(topic)
        }
    }

    override fun onLeftChannel(topic: SocketTopic) {
        with(topic) {
            channelSet.remove(this)
            runIfEmptyChannel {
                socketHeartbeat.period = period
                socketHeartbeat.stopInterval()
                socketClient.closeConnection(SocketStatusCode.NORMAL, "Disconnected successfully")
            }
        }
    }

    override fun setConnectionListener(connectionListener: SocketConnectionCallback?) {
        socketDispatcher.setSocketConnectionCallback(connectionListener)
    }

    override fun setChannelListener(channelListener: SocketChannelCallback?) {
        socketDispatcher.setSocketChannelCallback(channelListener)
    }

    override fun setCustomEventListener(customEventListener: SocketCustomEventCallback?) {
        socketDispatcher.setSocketCustomEVentCallback(customEventListener)
    }

    private inline fun runIfEmptyChannel(doSomething: () -> Unit) {
        if (channelSet.isEmpty()) {
            doSomething()
        }
    }
}
