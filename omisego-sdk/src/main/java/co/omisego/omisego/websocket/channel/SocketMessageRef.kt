package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class SocketMessageRef : SocketChannelContract.MessageRef {
    override var scheme: String = ""
    override var value: String = "0"
        get() {
            val incremental = "${field.toInt() + 1}"
            field = incremental
            return "$scheme:$incremental"
        }

    companion object {
        const val SCHEME_JOIN: String = "join"
        const val SCHEME_HEARTBEAT: String = "heartbeat"
    }
}
