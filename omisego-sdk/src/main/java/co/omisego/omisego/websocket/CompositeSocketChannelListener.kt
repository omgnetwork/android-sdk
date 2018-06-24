package co.omisego.omisego.websocket

import co.omisego.omisego.model.APIError

class CompositeSocketChannelListener(
    private val listeners: MutableSet<SocketChannelListener> = linkedSetOf()
) : SocketChannelListener, MutableSet<SocketChannelListener> by listeners {

    override fun onJoinedChannel(topic: String) {
        listeners.forEach { it.onJoinedChannel(topic) }
    }

    override fun onLeftChannel(topic: String) {
        listeners.forEach { it.onLeftChannel(topic) }
    }

    override fun onError(apiError: APIError) {
        listeners.forEach { it.onError(apiError) }
    }
}
