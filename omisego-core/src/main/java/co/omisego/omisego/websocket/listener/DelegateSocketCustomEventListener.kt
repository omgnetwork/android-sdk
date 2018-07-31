package co.omisego.omisego.websocket.listener

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 25/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.strategy.FilterStrategy

class DelegateSocketCustomEventListener(
    override val strategy: FilterStrategy,
    val delegate: SocketCustomEventListener
) : SimpleSocketCustomEventListener() {
    override fun onSpecificEvent(event: SocketEvent<*>) {
        delegate.onEvent(event)
    }
}
