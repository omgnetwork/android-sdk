package co.omisego.omisego.websocket.listener.internal

/**
 * OmiseGO
 *
 * Created by Yannick Badoual on 7/13/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.websocket.listener.SocketChannelListener

interface SocketChannelListenerSet {
    /**
     * Add listener for subscribing to the [SocketChannelListener] event.
     *
     * @param channelListener The [SocketChannelListener] to be invoked when the channel has been joined, left, or got an error.
     * @see SocketChannelListener for the event detail.
     */
    fun addChannelListener(channelListener: SocketChannelListener)

    /**
     * Remove the listener for unsubscribing from the [SocketChannelListener] event.
     *
     * @param channelListener The [SocketChannelListener] to be unsubscribed.
     */
    fun removeChannelListener(channelListener: SocketChannelListener)
}
