package co.omisego.omisego.websocket.listener.internal

import co.omisego.omisego.websocket.listener.SocketCustomEventListener

/**
 * OmiseGO
 *
 * Created by Yannick Badoual on 7/13/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

interface SocketCustomEventListenerSet {
    fun addCustomEventListener(customEventListener: SocketCustomEventListener)
    fun removeCustomEventListener(customEventListener: SocketCustomEventListener)
}
