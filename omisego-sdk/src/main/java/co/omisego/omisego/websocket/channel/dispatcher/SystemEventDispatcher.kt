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
import co.omisego.omisego.websocket.enum.SocketSystemEvent

/**
 * A listener for dispatcher the [SocketConnectionListener] and [SocketChannelListener] events.
 */
class SystemEventDispatcher : SocketDispatcherContract.SystemEventDispatcher {

    override var socketChannelListener: SocketChannelListener? = null

    override var socketChannel: SocketDispatcherContract.SocketChannel? = null

    override fun handleEvent(systemEvent: SocketSystemEvent, response: SocketReceive) {
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

    private inline fun SocketTopic<*>.runIfFirstJoined(lambda: () -> Unit) {
        if (socketChannel?.joined(this.name) == false) {
            lambda()
        }
    }

    private inline fun SocketReceive.runIfRefSchemeIsJoined(lambda: () -> Unit) {
        val ref = this.ref ?: return
        if (ref.startsWith(SocketMessageRef.SCHEME_JOIN)) {
            lambda()
        }
    }
}
