package co.omisego.omisego.websocket.listener

import co.omisego.omisego.websocket.SocketConnectionListener

interface SocketConnectionListenerSet {
    fun addConnectionListener(connectionListener: SocketConnectionListener)
    fun removeConnectionListener(connectionListener: SocketConnectionListener)
}