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
import co.omisego.omisego.websocket.SocketChannelCallback
import co.omisego.omisego.websocket.SocketConnectionCallback
import co.omisego.omisego.websocket.channel.SocketMessageRef
import co.omisego.omisego.websocket.channel.dispatcher.SocketDispatcherContract.SocketChannel
import co.omisego.omisego.websocket.enum.SocketSystemEvent

/**
 * A callback for dispatcher the [SocketConnectionCallback] and [SocketChannelCallback] events.
 */
class SystemEventDispatcher : SocketDispatcherContract.SystemEventDispatcher {
    /**
     * A connection callback that will be used for dispatch the [SocketConnectionCallback] events.
     */
    override var socketConnectionCallback: SocketConnectionCallback? = null

    /**
     * A channel callback that be used for dispatch the [SocketChannelCallback] events.
     */
    override var socketChannelCallback: SocketChannelCallback? = null

    /**
     * A socketChannel for delegate the event to the [SocketChannel] internally for further handling the event.
     */
    override var socketChannel: SocketDispatcherContract.SocketChannel? = null

    /**
     * The web socket replied object from eWallet API.
     */
    override var socketReceive: SocketReceive? = null

    /**
     * Handles the [SocketSystemEvent] and may dispatch the [SocketChannelCallback] or [SocketConnectionCallback] to the client.
     *
     * @param systemEvent To indicate which event of the [SocketSystemEvent]
     */
    override fun handleEvent(systemEvent: SocketSystemEvent) {
        val response = socketReceive ?: return
        when (systemEvent) {
            SocketSystemEvent.CLOSE -> {
                val topic = SocketTopic(response.topic)
                response.runIfRefSchemeIsJoined {
                    socketChannel?.onLeftChannel(topic)
                    socketChannelCallback?.onLeftChannel(topic)
                }
            }
            SocketSystemEvent.REPLY -> {
                val topic = SocketTopic(response.topic)
                response.runIfRefSchemeIsJoined {
                    topic.runIfFirstJoined {
                        socketChannel?.onJoinedChannel(topic)
                        socketChannelCallback?.onJoinedChannel(topic)
                    }
                }
            }
            SocketSystemEvent.ERROR -> {
                socketChannelCallback?.onError(APIError(ErrorCode.SDK_SOCKET_ERROR, "Something goes wrong while connecting to the channel"))
            }
        }
    }

    /**
     * Run the lambda when the topic hasn't joined yet
     */
    private inline fun SocketTopic.runIfFirstJoined(lambda: () -> Unit) {
        if (socketChannel?.joined(this) == false) {
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
