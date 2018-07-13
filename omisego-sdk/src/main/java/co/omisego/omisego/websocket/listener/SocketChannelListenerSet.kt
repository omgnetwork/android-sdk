package co.omisego.omisego.websocket.listener

/**
 * OmiseGO
 *
 * Created by Yannick Badoual on 7/13/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

interface SocketChannelListenerSet {
    fun addChannelListener(channelListener: SocketChannelListener)
    fun removeChannelListener(channelListener: SocketChannelListener)
}
