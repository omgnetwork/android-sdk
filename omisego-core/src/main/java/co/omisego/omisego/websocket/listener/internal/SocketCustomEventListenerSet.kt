package co.omisego.omisego.websocket.listener.internal

/**
 * OmiseGO
 *
 * Created by Yannick Badoual on 7/13/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.websocket.listener.SocketChannelListener
import co.omisego.omisego.websocket.listener.SocketCustomEventListener

interface SocketCustomEventListenerSet {
    /**
     * Add listener for subscribing to the [SocketCustomEventListener] event.
     *
     * @param customEventListener The [SocketCustomEventListener] to be invoked when the custom event is coming.
     * Learn more: https://github.com/omisego/ewallet/blob/master/docs/guides/ewallet_api_websockets.md#receivable-custom-events
     */
    fun addCustomEventListener(customEventListener: SocketCustomEventListener)

    /**
     * Remove the listener for unsubscribing from the [SocketCustomEventListener] event.
     *
     * @param customEventListener The [SocketChannelListener] to be unsubscribed.
     */
    fun removeCustomEventListener(customEventListener: SocketCustomEventListener)
}
