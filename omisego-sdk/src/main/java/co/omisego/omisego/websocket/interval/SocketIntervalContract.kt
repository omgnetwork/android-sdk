package co.omisego.omisego.websocket.interval

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.SocketMessageRef
import java.util.Timer

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
interface SocketIntervalContract {
    var timer: Timer?
    val socketMessageRef: SocketMessageRef

    fun startInterval(task: (SocketSend) -> Unit)
    fun stopInterval()
}