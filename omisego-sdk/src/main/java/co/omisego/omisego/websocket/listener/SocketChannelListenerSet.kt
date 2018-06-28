package co.omisego.omisego.websocket.listener

import co.omisego.omisego.websocket.SocketChannelListener

interface SocketChannelListenerSet {
    fun addChannelListener(channelListener: SocketChannelListener)
    fun removeChannelListener(channelListener: SocketChannelListener)
}