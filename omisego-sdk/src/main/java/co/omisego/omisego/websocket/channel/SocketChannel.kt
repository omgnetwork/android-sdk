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
import co.omisego.omisego.websocket.channel.SocketChannelContract.Dispatcher
import co.omisego.omisego.websocket.channel.SocketChannelContract.SocketClient
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcherContract
import co.omisego.omisego.websocket.channel.interval.SocketHeartbeat
import co.omisego.omisego.websocket.enum.SocketEventSend
import co.omisego.omisego.websocket.enum.SocketStatusCode
import okhttp3.WebSocketListener

internal class SocketChannel(
    /**
     * A [Dispatcher] is responsible dispatch all events to the client.
     */
    override val socketDispatcher: SocketChannelContract.Dispatcher,

    /**
     * A [SocketClient] for sending a message to the eWallet web socket API and close the web socket connection.
     */
    override val socketClient: SocketChannelContract.SocketClient,

    /**
     * A [SocketMessageRef] is responsible for create unique ref value to be included in the [SocketSend] request.
     */
    override val socketMessageRef: SocketChannelContract.MessageRef = SocketMessageRef()
) : SocketClientContract.Channel, SocketChannelContract.Channel, SocketDispatcherContract.SocketChannel {
    private val channelSet: MutableSet<SocketTopic> by lazy { mutableSetOf<SocketTopic>() }

    /**
     * An interval of milliseconds for scheduling the interval event such as the heartbeat event which used for keeping the connection alive.
     * Default 5,000 milliseconds.
     */
    override var period: Long = 5000

    /**
     * A [SocketHeartbeat] is responsible for schedule sending the heartbeat event for keep the connection alive
     */
    override val socketHeartbeat: SocketChannelContract.SocketInterval by lazy { SocketHeartbeat(socketMessageRef) }

    /**
     * Send [SocketEventSend.JOIN] event to the server. Do nothing if the channel has already joined.
     *
     * @param topic Join the channel by the given topic.
     * @param payload (Optional) the additional data you might want to send bundled with the request.
     */
    override fun join(topic: SocketTopic, payload: Map<String, Any>) {
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
    override fun leave(topic: SocketTopic, payload: Map<String, Any>) {
        if (joined(topic)) {
            socketClient.send(createLeaveMessage(topic, payload))
        }
    }

    /**
     * Retrieves a set of active [SocketTopic].
     *
     * @return A set of active [SocketTopic].
     */
    override fun retrieveChannels(): Set<SocketTopic> = channelSet

    /**
     * Retrieves the [WebSocketListener]  to be used for initializing the [Websocket] in the [SocketClient].
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
    override fun joined(topic: SocketTopic) = channelSet.contains(topic)

    /**
     * Create a [SocketSend] instance to be used for join a channel.
     *
     * @param topic A topic indicating which channel will be joined.
     * @param payload (Optional) the additional data you might want to send bundled with the request.
     *
     * @return A [SocketSend] instance used for joining the channel.
     */
    override fun createJoinMessage(topic: SocketTopic, payload: Map<String, Any>): SocketSend =
        SocketSend(topic.name, SocketEventSend.JOIN, socketMessageRef.value, payload)

    /**
     * Create a [SocketSend] instance to be used for join a channel.
     *
     * @param topic A topic indicating which channel will be joined.
     * @param payload (Optional) the additional data you might want to send bundled with the request.
     *
     * @return A [SocketSend] instance used for leaving the channel.
     */
    override fun createLeaveMessage(topic: SocketTopic, payload: Map<String, Any>): SocketSend =
        SocketSend(topic.name, SocketEventSend.LEAVE, socketMessageRef.value, payload)

    /**
     * Invoked when the client have been joined the channel successfully.
     *
     * @param topic A topic indicating which channel will be joined.
     */
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

    /**
     * Invoked when the client have been left the channel successfully.
     *
     * @param topic A topic indicating which channel will be joined.
     */
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

    /**
     * Subscribe to the [SocketConnectionCallback] event.
     *
     * @param connectionListener The [SocketConnectionCallback] to be invoked when the web socket connection is connected or disconnected
     */
    override fun setConnectionListener(connectionListener: SocketConnectionCallback?) {
        socketDispatcher.setSocketConnectionCallback(connectionListener)
    }

    /**
     * Subscribe to the [SocketChannelCallback] event.
     *
     * @param channelListener The [SocketChannelCallback] to be invoked when the web socket channel has been joined, left or got an error.
     */
    override fun setChannelListener(channelListener: SocketChannelCallback?) {
        socketDispatcher.setSocketChannelCallback(channelListener)
    }

    /**
     * Subscribe to the [SocketCustomEventCallback] event.
     *
     * @param customEventListener The [SocketCustomEventCallback] to be invoked when the [CustomEvent] event happened.
     *
     */
    override fun setCustomEventListener(customEventListener: SocketCustomEventCallback?) {
        socketDispatcher.setSocketCustomEventCallback(customEventListener)
    }

    private inline fun runIfEmptyChannel(doSomething: () -> Unit) {
        if (channelSet.isEmpty()) {
            doSomething()
        }
    }
}
