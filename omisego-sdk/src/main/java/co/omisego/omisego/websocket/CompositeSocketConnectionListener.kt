package co.omisego.omisego.websocket

class CompositeSocketConnectionListener(
    private val listeners: MutableSet<SocketConnectionListener> = linkedSetOf()
) : SocketConnectionListener, MutableSet<SocketConnectionListener> by listeners {

    override fun onConnected() {
        listeners.forEach { it.onConnected() }
    }

    override fun onDisconnected(throwable: Throwable?) {
        listeners.forEach { it.onDisconnected(throwable) }
    }
}