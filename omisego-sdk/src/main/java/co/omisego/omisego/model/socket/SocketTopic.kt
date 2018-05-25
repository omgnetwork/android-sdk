package co.omisego.omisego.model.socket

import co.omisego.omisego.websocket.SocketCustomEventCallback
import co.omisego.omisego.websocket.interval.SocketHeartbeat

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

data class SocketTopic<T : SocketCustomEventCallback>(val name: String)

/**
 * Run the lambda when the topic is coming from the user (to exclude the heartbeat event).
 */
internal inline fun <T : SocketCustomEventCallback> SocketTopic<T>.runIfNotInternalTopic(lambda: SocketTopic<T>.() -> Unit) {
    if (this.name != SocketHeartbeat.EVENT_NAME) {
        lambda()
    }
}