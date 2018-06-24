package co.omisego.omisego.websocket

import co.omisego.omisego.model.APIError

class CompositeSocketChannelListener(
    private val listeners: MutableSet<SocketChannelListener> = linkedSetOf()
) : SocketChannelListener, MutableSet<SocketChannelListener> by listeners {

    override fun onJoinedChannel(topic: String): Boolean {
        listeners.forEach {
            if (it.onJoinedChannel(topic)) return true
        }
        return false
    }

    override fun onLeftChannel(topic: String): Boolean {
        listeners.forEach {
            if (it.onLeftChannel(topic)) return true
        }
        return false
    }

    override fun onError(apiError: APIError): Boolean {
        listeners.forEach {
            if (it.onError(apiError)) return true
        }
        return false
    }
}
