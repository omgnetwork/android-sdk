package co.omisego.omisego.websocket.listener

import co.omisego.omisego.websocket.SocketCustomEventListener

interface SocketCustomEventListenerSet {
    fun addCustomEventListener(customEventListener: SocketCustomEventListener)
    fun removeCustomEventListener(customEventListener: SocketCustomEventListener)
}