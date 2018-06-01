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
import okhttp3.WebSocketListener

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
    private val channelSet: MutableSet<String> by lazy { mutableSetOf<String>() }

    override val socketMessageRef: SocketChannelContract.MessageRef = SocketMessageRef(scheme = SocketMessageRef.SCHEME_JOIN)

    override var period: Long = 5000

    override fun join(topic: String, payload: Map<String, Any>) {
        if (!joined(topic)) {
            socketClient.send(createJoinMessage(topic, payload))
        }
    }

    override fun leave(topic: String, payload: Map<String, Any>) {
        if (joined(topic)) {
            socketClient.send(createLeaveMessage(topic, payload))
        }
    }

    override fun leaveAll() {
        for (channel in channelSet) {
            leave(channel, mapOf())
        }
    }

    override fun retrieveChannels(): Set<String> = channelSet.toSet()

    override fun retrieveWebSocketListener(): WebSocketListener = socketDispatcher.retrieveWebSocketListener()

    override fun joined(topic: String) = channelSet.contains(topic)

    override fun createJoinMessage(topic: String, payload: Map<String, Any>): SocketSend =
        SocketSend(topic, SocketEventSend.JOIN, socketMessageRef.value, payload)

    override fun createLeaveMessage(topic: String, payload: Map<String, Any>): SocketSend =
        SocketSend(topic, SocketEventSend.LEAVE, null, payload)

    override fun onJoinedChannel(topic: String) {
        runIfEmptyChannel {
            socketClient.socketHeartbeat.startInterval {
                socketClient.send(it)
            }
        }
        channelSet.add(topic)
    }

    override fun onLeftChannel(topic: String) {
        with(topic) {
            channelSet.remove(this)
            runIfEmptyChannel {
                socketClient.socketHeartbeat.period = period
                socketClient.socketHeartbeat.stopInterval()
                socketClient.closeConnection(SocketStatusCode.NORMAL, "Disconnected successfully")
            }
        }
    }

    override fun setConnectionListener(connectionListener: SocketConnectionListener?) {
        socketDispatcher.setSocketConnectionListener(connectionListener)
    }

    override fun setChannelListener(channelListener: SocketChannelListener?) {
        socketDispatcher.setSocketChannelListener(channelListener)
    }

    override fun setCustomEventListener(customEventListener: SocketCustomEventListener?) {
        socketDispatcher.setSocketCustomEventListener(customEventListener)
    }

    private inline fun runIfEmptyChannel(doSomething: () -> Unit) {
        if (channelSet.isEmpty()) {
            doSomething()
        }
    }
}
