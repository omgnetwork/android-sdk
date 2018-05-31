package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.model.socket.SocketTopic
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

    /**
     * A [SocketMessageRef] is responsible for create unique ref value to be included in the [SocketSend] request.
     */
    override val socketMessageRef: SocketChannelContract.MessageRef = SocketMessageRef(scheme = SocketMessageRef.SCHEME_JOIN)

    /**
     * An interval of milliseconds for scheduling the interval event such as the heartbeat event which used for keeping the connection alive.
     * Default 5,000 milliseconds.
     */
    override var period: Long = 5000

    /**
     * Send [SocketEventSend.JOIN] event to the server. Do nothing if the channel has already joined.
     *
     * @param topic Join the channel by the given topic.
     * @param payload (Optional) the additional data you might want to send bundled with the request.
     */
    override fun join(topic: String, payload: Map<String, Any>) {
        if (!joined(topic)) {
            socketClient.send(createJoinMessage(topic, payload))
        }
    }

    /**
     * Send [SocketEventSend.LEAVE] event to the server. Do nothing if the channel has already left.
     *
     * @param topic Leave from the channel by the given topic.
     * @param payload (Optional) payload you want to send along with the request/.
     */
    override fun leave(topic: String, payload: Map<String, Any>) {
        if (joined(topic)) {
            socketClient.send(createLeaveMessage(topic, payload))
        }
    }

    /**
     * Send leave event for all currently active channels.
     */
    override fun leaveAll() {
        for (channel in channelSet) {
            leave(channel, mapOf())
        }
    }

    /**
     * Retrieves a set of active [SocketTopic].
     *
     * @return A set of active [SocketTopic].
     */
    override fun retrieveChannels(): Set<String> = channelSet.toSet()

    /**
     * Retrieves the [WebSocketListener] to be used for initializing the [WebSocket] in the [SocketClient].
     *
     * @return [WebSocketListener]
     */
    override fun retrieveWebSocketListener(): WebSocketListener = socketDispatcher.retrieveWebSocketListener()

    /**
     * Returns a boolean indicating if the channel is joined.
     *
     * @param topic A topic indicating which channel will be joined.
     * @return A boolean indicating if the channel is joined.
     */
    override fun joined(topic: String) = channelSet.contains(topic)

    /**
     * Create a [SocketSend] instance to be used for join a channel.
     *
     * @param topic A topic indicating which channel will be joined.
     * @param payload (Optional) the additional data you might want to send bundled with the request.
     *
     * @return A [SocketSend] instance used for joining the channel.
     */
    override fun createJoinMessage(topic: String, payload: Map<String, Any>): SocketSend =
        SocketSend(topic, SocketEventSend.JOIN, socketMessageRef.value, payload)

    /**
     * Create a [SocketSend] instance to be used for join a channel.
     *
     * @param topic A topic indicating which channel will be joined.
     * @param payload (Optional) the additional data you might want to send bundled with the request.
     *
     * @return A [SocketSend] instance used for leaving the channel.
     */
    override fun createLeaveMessage(topic: String, payload: Map<String, Any>): SocketSend =
        SocketSend(topic, SocketEventSend.LEAVE, null, payload)

    /**
     * Invoked when the client have been joined the channel successfully.
     *
     * @param topic A topic indicating which channel will be joined.
     */
    override fun onJoinedChannel(topic: String) {
        runIfEmptyChannel {
            socketClient.socketHeartbeat.startInterval {
                socketClient.send(it)
            }
        }
        channelSet.add(topic)
    }

    /**
     * Invoked when the client have been left the channel successfully.
     *
     * @param topic A topic indicating which channel will be joined.
     */
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

    /**
     * Subscribe to the [SocketConnectionListener] event.
     *
     * @param connectionListener The [SocketConnectionListener] to be invoked when the web socket connection is connected or disconnected
     */
    override fun setConnectionListener(connectionListener: SocketConnectionListener?) {
        socketDispatcher.setSocketConnectionListener(connectionListener)
    }

    /**
     * Subscribe to the [SocketChannelListener] event.
     *
     * @param channelListener The [SocketChannelListener] to be invoked when the web socket channel has been joined, left or got an error.
     */
    override fun setChannelListener(channelListener: SocketChannelListener?) {
        socketDispatcher.setSocketChannelListener(channelListener)
    }

    /**
     * Subscribe to the [SocketCustomEventListener] event.
     *
     * @param customEventListener The [SocketCustomEventListener] to be invoked when the [CustomEvent] event happened.
     *
     */
    override fun setCustomEventListener(customEventListener: SocketCustomEventListener?) {
        socketDispatcher.setSocketCustomEventListener(customEventListener)
    }

    private inline fun runIfEmptyChannel(doSomething: () -> Unit) {
        if (channelSet.isEmpty()) {
            doSomething()
        }
    }
}
