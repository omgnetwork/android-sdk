package co.omisego.omisego.model.socket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.websocket.enum.SocketEventSend

data class SocketSend(val topic: String, val event: SocketEventSend, val ref: String, val data: Map<String, Any>)
