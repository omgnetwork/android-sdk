package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.listener.SocketChannelListener
import co.omisego.omisego.websocket.listener.SocketCustomEventListener

/**
 * A listener for dispatch the [SocketCustomEventListener] events.
 */
class CustomEventDispatcher(
    override val socketChannelListener: SocketChannelListener,
    override val customEventListeners: MutableSet<SocketCustomEventListener> = linkedSetOf()
) : SocketDispatcherContract.CustomEventDispatcher {

    override fun handleEvent(customEvent: SocketCustomEvent, response: SocketReceive<*>) {
        val event = customEvent.eventBuilder(response)
        if (event == null) {
            // Null event means the event is not implemented, or the data don't match the event type
            response.error?.let(socketChannelListener::onError)
            return
        }
        customEventListeners.forEach { it.onEvent(event) }
    }
}
