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
import co.omisego.omisego.websocket.enum.SocketStatusCode
import co.omisego.omisego.websocket.enum.SocketSystemEvent
import okhttp3.Response
import java.net.SocketException

/**
 * A listener for dispatcher the [SocketConnectionListener] and [SocketChannelListener] events.
 */
class SystemEventDispatcher : SocketDispatcherContract.SystemEventDispatcher {
    override var socketConnectionListener: SocketConnectionListener? = null

    override var socketChannelListener: SocketChannelListener? = null

    override var socketChannel: SocketDispatcherContract.SocketChannel? = null

    override var socketReceive: SocketReceive? = null

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

    override fun handleSocketFailure(throwable: Throwable, response: Response?) {
        socketConnectionListener?.onDisconnected(throwable)
    }

    override fun handleSocketOpened(response: Response) {
        socketConnectionListener?.onConnected()
    }

    override fun handleSocketClosed(code: Int, reason: String) {
        if (code == SocketStatusCode.NORMAL.code)
            socketConnectionListener?.onDisconnected(null)
        else {
            socketConnectionListener?.onDisconnected(SocketException("$code $reason"))
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
