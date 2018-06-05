package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.SocketChannelListener
import co.omisego.omisego.websocket.SocketClientContract
import co.omisego.omisego.websocket.SocketConnectionListener
import co.omisego.omisego.websocket.SocketCustomEventListener
import co.omisego.omisego.websocket.channel.SocketChannelContract.Dispatcher
import co.omisego.omisego.websocket.channel.SocketChannelContract.SocketClient
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcherContract
import co.omisego.omisego.websocket.enum.SocketEventSend
import co.omisego.omisego.websocket.enum.SocketStatusCode
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A SocketChannel is responsible for handling join or leave from the socket channel.
 *
 * @param socketDispatcher A [Dispatcher] is responsible dispatch all events to the client.
 * @param socketClient A [SocketClient] for sending a message to the eWallet web socket API and close the web socket connection.
 */
internal class SocketChannel(
    override val socketDispatcher: SocketChannelContract.Dispatcher,
    override val socketClient: SocketChannelContract.SocketClient
) : SocketClientContract.Channel, SocketChannelContract.Channel, SocketDispatcherContract.SocketChannel {
    override val pendingChannelsQueue: BlockingQueue<SocketSend> = ArrayBlockingQueue<SocketSend>(1000, true)
    private val channelSet: MutableSet<String> by lazy { mutableSetOf<String>() }

    override val leavingChannels: AtomicBoolean by lazy { AtomicBoolean(false) }
    override var period: Long = 5000
    override val socketMessageRef: SocketChannelContract.MessageRef = SocketMessageRef(scheme = SocketMessageRef.SCHEME_JOIN)

    override fun join(topic: String, payload: Map<String, Any>) {
        whenNeverJoinedTopic(topic) {
            val socketSend = createJoinMessage(topic, payload)
            whenNotAbleToJoinChannel {
                pendingChannelsQueue.add(socketSend)
                return
            }
            socketClient.send(socketSend)
        }
    }

    override fun leave(topic: String, payload: Map<String, Any>) {
        if (joined(topic)) {
            socketClient.send(createLeaveMessage(topic, payload))
        }
    }

    override fun leaveAll() {
        leavingChannels.set(true)
        for (channel in channelSet) {
            leave(channel, mapOf())
        }
    }

    override fun executePendingJoinChannel() {
        for (socketSend in pendingChannelsQueue) {
            join(socketSend.topic, socketSend.data)
        }
    }

    override fun hasSentAllPendingJoinChannel(): Boolean = pendingChannelsQueue.isEmpty()

    override fun joinable(): Boolean = !leavingChannels.get()

    override fun joined(topic: String) = channelSet.contains(topic)

    override fun createJoinMessage(topic: String, payload: Map<String, Any>): SocketSend =
        SocketSend(topic, SocketEventSend.JOIN, socketMessageRef.value, payload)

    override fun createLeaveMessage(topic: String, payload: Map<String, Any>): SocketSend =
        SocketSend(topic, SocketEventSend.LEAVE, null, payload)

    override fun retrieveChannels(): Set<String> = channelSet.toSet()

    override fun onJoinedChannel(topic: String) {
        runIfEmptyChannel {
            socketClient.socketHeartbeat.startInterval {
                socketClient.send(it)
            }
        }
        channelSet.add(topic)
        val socketSend = pendingChannelsQueue.findLast { it.topic == topic } ?: return
        pendingChannelsQueue.remove(socketSend)
    }

    override fun onLeftChannel(topic: String) {
        with(topic) {
            channelSet.remove(this)
            runIfEmptyChannel {
                socketClient.socketHeartbeat.period = period
                socketClient.socketHeartbeat.stopInterval()
                pendingChannelsQueue.clear()
                socketDispatcher.clearCustomEventListenerMap()
                leavingChannels.set(false)
                socketClient.closeConnection(SocketStatusCode.NORMAL, "Disconnected successfully")
            }
        }
    }

    override fun onSocketOpened() {
        leavingChannels.set(false)
    }

    override fun setConnectionListener(connectionListener: SocketConnectionListener?) {
        socketDispatcher.setSocketConnectionListener(connectionListener)
    }

    override fun setChannelListener(channelListener: SocketChannelListener?) {
        socketDispatcher.setSocketChannelListener(channelListener)
    }

    override fun addCustomEventListener(topic: String, customEventListener: SocketCustomEventListener) {
        socketDispatcher.addCustomEventListener(topic, customEventListener)
    }

    private inline fun runIfEmptyChannel(doSomething: () -> Unit) {
        if (channelSet.isEmpty()) {
            doSomething()
        }
    }

    private inline fun whenNeverJoinedTopic(topic: String, doSomething: () -> Unit) {
        if (!joined(topic)) {
            doSomething()
        }
    }

    private inline fun whenNotAbleToJoinChannel(doSomething: () -> Unit) {
        if (!joinable()) {
            doSomething()
        }
    }
}
