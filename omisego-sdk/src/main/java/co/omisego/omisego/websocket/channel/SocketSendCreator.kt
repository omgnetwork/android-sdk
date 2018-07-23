package co.omisego.omisego.websocket.channel

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.websocket.enum.SocketEventSend

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 22/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class SocketSendCreator(override val socketMessageRef: SocketMessageRef) : SocketChannelContract.SocketSendCreator {
    override fun createJoinMessage(topic: String, payload: Map<String, Any>): SocketSend =
        SocketSend(topic, SocketEventSend.JOIN, socketMessageRef.value, payload)

    override fun createLeaveMessage(topic: String, payload: Map<String, Any>): SocketSend =
        SocketSend(topic, SocketEventSend.LEAVE, null, payload)
}
