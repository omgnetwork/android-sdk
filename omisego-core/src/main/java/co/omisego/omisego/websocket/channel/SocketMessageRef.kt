package co.omisego.omisego.websocket.channel

import java.util.concurrent.atomic.AtomicInteger

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class SocketMessageRef(override val scheme: String) : SocketChannelContract.MessageRef {
    private val _value = AtomicInteger(0)
    override val value: String
        get() = "$scheme:${_value.incrementAndGet()}"

    companion object {
        const val SCHEME_JOIN: String = "join"
        const val SCHEME_LEAVE: String = "leave"
        const val SCHEME_HEARTBEAT: String = "heartbeat"
    }
}
