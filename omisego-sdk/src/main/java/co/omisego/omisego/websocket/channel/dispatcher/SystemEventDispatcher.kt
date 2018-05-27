package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.model.socket.SocketTopic
import co.omisego.omisego.websocket.SocketChannelListener
import co.omisego.omisego.websocket.SocketConnectionListener
import co.omisego.omisego.websocket.SocketCustomEventListener
import co.omisego.omisego.websocket.channel.SocketMessageRef
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcherContract.SocketChannel
import co.omisego.omisego.websocket.enum.SocketSystemEvent

/**
 * A listener for dispatcher the [SocketConnectionListener] and [SocketChannelListener] events.
 */
class SystemEventDispatcher : SocketDispatcherContract.SystemEventDispatcher {
    /**
     * A connection listener that will be used for dispatch the [SocketConnectionListener] events.
     */
    override var socketConnectionListener: SocketConnectionListener? = null

    /**
     * A channel listener that be used for dispatch the [SocketChannelListener] events.
     */
    override var socketChannelListener: SocketChannelListener? = null

    /**
     * A socketChannel for delegate the event to the [SocketChannel] internally for further handling the event.
     */
    override var socketChannel: SocketDispatcherContract.SocketChannel? = null

    /**
     * The web socket replied object from eWallet API.
     */
    override var socketReceive: SocketReceive? = null

    /**
     * Handles the [SocketSystemEvent] and may dispatch the [SocketChannelListener] or [SocketConnectionListener] to the client.
     *
     * @param systemEvent To indicate which event of the [SocketSystemEvent]
     */
    override fun handleEvent(systemEvent: SocketSystemEvent) {
        val response = socketReceive ?: return
        when (systemEvent) {
            SocketSystemEvent.CLOSE -> {
                response.runIfRefSchemeIsJoined {
                    socketChannel?.onLeftChannel(response.topic)
                    socketChannelListener?.onLeftChannel(response.topic)
                }
            }
            SocketSystemEvent.REPLY -> {
                val topic = SocketTopic<SocketCustomEventListener>(response.topic)
                response.runIfRefSchemeIsJoined {
                    topic.runIfFirstJoined {
                        socketChannel?.onJoinedChannel(topic.name)
                        socketChannelListener?.onJoinedChannel(topic.name)
                    }
                }
            }
            SocketSystemEvent.ERROR -> {
                socketChannelListener?.onError(APIError(ErrorCode.SDK_SOCKET_ERROR, "Something goes wrong while connecting to the channel"))
            }
        }
    }

    /**
     * Run the lambda when the topic hasn't joined yet
     */
    private inline fun SocketTopic<*>.runIfFirstJoined(lambda: () -> Unit) {
        if (socketChannel?.joined(this.name) == false) {
            lambda()
        }
    }

    /**
     * Run the lambda when the ref starts with "join"
     */
    private inline fun SocketReceive.runIfRefSchemeIsJoined(lambda: () -> Unit) {
        val ref = this.ref ?: return
        if (ref.startsWith(SocketMessageRef.SCHEME_JOIN)) {
            lambda()
        }
    }
}
