package co.omisego.omisego.websocket.interval

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 21/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.SocketClientContract
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.SocketMessageRef
import java.util.Timer

abstract class BaseSocketInterval : SocketClientContract.SocketInterval {
    /**
     * [SocketMessageRef] is responsible for creating a unique ref value to be sent with the [SocketSend].
     */
    override val socketMessageRef: SocketChannelContract.MessageRef = SocketMessageRef("")
    /**
     * The timer for scheduling the [SocketSend] periodically to be sent to the server.
     */
    override var timer: Timer? = null

    /**
     * An interval of milliseconds between the end of the previous task and the start of the next one.
     */
    override var period: Long = 5000

    override fun stopInterval() {
        synchronized(this) {
            timer?.cancel()
            timer = null
        }
    }
}