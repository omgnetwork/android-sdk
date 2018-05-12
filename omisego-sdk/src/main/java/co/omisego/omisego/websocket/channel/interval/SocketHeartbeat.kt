package co.omisego.omisego.websocket.channel.interval

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.SocketMessageRef
import co.omisego.omisego.websocket.enum.SocketEventSend
import java.util.Date
import java.util.Timer
import kotlin.concurrent.schedule

class SocketHeartbeat(
    override val socketMessageRef: SocketMessageRef
) : SocketChannelContract.SocketInterval {
    override var timer: Timer? = null

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun startInterval(crossinline task: (SocketSend) -> Unit) {
        timer = Timer()
        timer?.schedule(Date(), 5000, {
            task(SocketSend(EVENT_NAME, SocketEventSend.HEARTBEAT, socketMessageRef.value, mapOf()))
        })
    }

    override fun stopInterval() {
        timer?.cancel()
        timer = null
    }

    companion object {
        const val EVENT_NAME = "phoenix"
    }
}