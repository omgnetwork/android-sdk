package co.omisego.omisego.model.socket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.websocket.channel.interval.SocketHeartbeat

data class SocketTopic(val name: String)

/**
 * Run the lambda when the topic is coming from the user (to exclude the heartbeat event).
 */
internal inline fun SocketTopic.runIfNotInternalTopic(lambda: SocketTopic.() -> Unit) {
    if (this.name != SocketHeartbeat.EVENT_NAME) {
        lambda()
    }
}