package co.omisego.omisego.websocket.interval

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.SocketMessageRef
import co.omisego.omisego.websocket.enum.SocketEventSend
import java.util.Date
import java.util.Timer
import kotlin.concurrent.schedule

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class SocketHeartbeat(
    override val socketMessageRef: SocketMessageRef
) : SocketIntervalContract {
    override var timer: Timer? = null

    override fun startInterval(task: (SocketSend) -> Unit) {
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