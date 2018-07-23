package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.websocket.SocketClientContract
import co.omisego.omisego.websocket.channel.SocketChannelContract.Dispatcher
import co.omisego.omisego.websocket.channel.SocketChannelContract.SocketClient
import co.omisego.omisego.websocket.enum.SocketStatusCode
import co.omisego.omisego.websocket.interval.SocketReconnect
import co.omisego.omisego.websocket.listener.SocketChannelListener
import co.omisego.omisego.websocket.listener.SocketConnectionListener
import co.omisego.omisego.websocket.listener.internal.CompositeSocketChannelListener
import co.omisego.omisego.websocket.listener.internal.CompositeSocketConnectionListener
import co.omisego.omisego.websocket.listener.internal.SocketChannelListenerSet
import co.omisego.omisego.websocket.listener.internal.SocketConnectionListenerSet
import co.omisego.omisego.websocket.listener.internal.SocketCustomEventListenerSet
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.SSLException

/**
 * A SocketChannel is responsible for handling join or leave from the socket channel.
 *
 * @param socketDispatcher A [Dispatcher] is responsible dispatch all events to the client.
 * @param socketClient A [SocketClient] for sending a message to the eWallet web socket API and close the web socket connection.
 */

internal class SocketChannel(
    override val socketDispatcher: SocketChannelContract.Dispatcher,
    override val socketClient: SocketChannelContract.SocketClient,
    override val compositeSocketConnectionListener: CompositeSocketConnectionListener,
    override val compositeSocketChannelListener: CompositeSocketChannelListener,
    override val socketReconnect: SocketReconnect = SocketReconnect(),
    override val socketPendingChannel: SocketPendingChannel = SocketPendingChannel(),
    override val socketSendCreator: SocketChannelContract.SocketSendCreator = SocketSendCreator(
        SocketMessageRef(scheme = SocketMessageRef.SCHEME_JOIN)
    )
) : SocketClientContract.Channel,
    SocketChannelContract.Channel,
    SocketConnectionListenerSet,
    SocketChannelListenerSet,
    SocketCustomEventListenerSet by socketDispatcher,
    SocketConnectionListener,
    SocketChannelListener {

    internal val channelSet: MutableSet<String> by lazy { mutableSetOf<String>() }
    override val leavingAllChannels: AtomicBoolean by lazy { AtomicBoolean(false) }
    override var period: Long = 5000

    init {
        compositeSocketConnectionListener.add(this)
        compositeSocketChannelListener.add(this)
    }

    override fun join(topic: String, payload: Map<String, Any>): Boolean {
        whenNeverJoinedChannel(topic) {
            val socketSend = socketSendCreator.createJoinMessage(topic, payload)

            whenLeavingAllChannelsInProgress {
                socketPendingChannel.add(socketSend, period)
                return false
            }

            socketReconnect.add(socketSend)
            return socketClient.send(socketSend)
        }
        return false
    }

    override fun leave(topic: String, payload: Map<String, Any>) {
        if (joined(topic)) {
            socketClient.send(socketSendCreator.createLeaveMessage(topic, payload))
        }
    }

    override fun leaveAll() {
        leavingAllChannels.set(true)
        for (channel in channelSet.toList()) {
            leave(channel, mapOf())
        }
    }

    override fun pending(): Boolean = socketPendingChannel.pendingChannelsQueue.isEmpty()

    fun joined(topic: String) = channelSet.contains(topic)

    override fun retrieveChannels(): Set<String> = channelSet.toSet()

    override fun onJoinedChannel(topic: String): Boolean {
        // We may already have received the event for the given topic
        if (joined(topic)) return true

        startHeartbeatWhenBegin()

        channelSet.add(topic)

        socketReconnect.stopReconnectIfDone(channelSet.toSet())

        socketPendingChannel.remove(topic)

        return false
    }

    override fun onLeftChannel(topic: String): Boolean {
        with(topic) {
            channelSet.remove(this)
            socketReconnect.remove(topic)
            whenChannelIsEmpty {
                disconnect(SocketStatusCode.NORMAL, "Disconnected successfully")
            }
        }
        return false
    }

    override fun disconnect(status: SocketStatusCode, reason: String) {
        socketClient.socketHeartbeat.period = period
        socketClient.socketHeartbeat.stopInterval()
        socketPendingChannel.pendingChannelsQueue.clear()
        channelSet.clear()
        leavingAllChannels.set(false)
        socketClient.closeConnection(status, reason)
        if (status == SocketStatusCode.NORMAL)
            socketDispatcher.clearCustomEventListeners()
    }

    override fun onError(apiError: APIError) = false

    override fun onConnected() {
        leavingAllChannels.set(false)
        socketPendingChannel.execute { topic, payload ->
            join(topic, payload)
        }
    }

    override fun onDisconnected(throwable: Throwable?) {
        when (throwable) {
            is SSLException -> startReconnect(throwable)
            else -> {
            }
        }
    }

    override fun addConnectionListener(connectionListener: SocketConnectionListener) {
        compositeSocketConnectionListener.add(connectionListener)
    }

    override fun removeConnectionListener(connectionListener: SocketConnectionListener) {
        compositeSocketConnectionListener.remove(connectionListener)
    }

    override fun addChannelListener(channelListener: SocketChannelListener) {
        compositeSocketChannelListener.add(channelListener)
    }

    override fun removeChannelListener(channelListener: SocketChannelListener) {
        compositeSocketChannelListener.remove(channelListener)
    }

    override fun startHeartbeatWhenBegin() {
        whenChannelIsEmpty {
            socketClient.socketHeartbeat.startInterval {
                socketClient.send(it)
            }
        }
    }

    override fun startReconnect(throwable: Throwable?) {
        disconnect(SocketStatusCode.CONNECTION_FAILURE, "Disconnected due to network connectivity change")
        socketReconnect.startInterval { socketSend ->
            whenNeverJoinedChannel(socketSend.topic) {
                socketClient.send(socketSend)
            }
        }
    }

    private inline fun whenChannelIsEmpty(doSomething: () -> Unit) {
        if (channelSet.isEmpty()) {
            doSomething()
        }
    }

    private inline fun whenNeverJoinedChannel(topic: String, doSomething: () -> Unit) {
        if (!joined(topic)) {
            doSomething()
        }
    }

    private inline fun whenLeavingAllChannelsInProgress(doSomething: () -> Unit) {
        if (leavingAllChannels.get()) {
            doSomething()
        }
    }
}
