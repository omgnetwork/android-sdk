package co.omisego.omisego.websocket.listener

/**
 * OmiseGO
 *
 * Created by Yannick Badoual on 7/13/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class CompositeSocketConnectionListener(
    private val listeners: MutableSet<SocketConnectionListener> = linkedSetOf()
) : SocketConnectionListener, MutableSet<SocketConnectionListener> by listeners {

    override fun onConnected() {
        forEach { it.onConnected() }
    }

    override fun onDisconnected(throwable: Throwable?) {
        forEach { it.onDisconnected(throwable) }
    }
}
