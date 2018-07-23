package co.omisego.omisego.websocket.interval

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
    /**
     * [SocketMessageRef] is responsible for creating a unique ref value to be sent with the [SocketSend].
     */
    override val socketMessageRef: SocketChannelContract.MessageRef
) : BaseSocketInterval() {
    /**
     * Start to schedule the [SocketSend] to be sent to the server periodically.
     *
     * @param task A lambda with a [SocketSend] parameter. This will be executed periodically that starts immediately.
     */
    @Synchronized
    inline fun startInterval(crossinline task: (SocketSend) -> Unit) {
        synchronized(this) {
            timer?.cancel()
            timer = Timer()
            timer?.schedule(Date(), period) {
                task(SocketSend(TOPIC, SocketEventSend.HEARTBEAT, socketMessageRef.value, mapOf()))
            }
        }
    }

    companion object {
        const val TOPIC = "phoenix"
    }
}
