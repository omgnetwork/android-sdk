package co.omisego.omisego.websocket.enum

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum

enum class SocketEventSend(override val value: String) : OMGEnum {
    HEARTBEAT("heartbeat"),
    JOIN("phx_join"),
    LEAVE("phx_leave");

    override fun toString(): String = this.value
}

enum class SocketEventReceive(override val value: String) : OMGEnum {
    CLOSE("phx_close"),
    ERROR("phx_error"),
    REPLY("phx_reply"),
    TRANSACTION_CONSUMPTION_REQUEST("transaction_consumption_request"),
    TRANSACTION_CONSUMPTION_FINALIZED("transaction_consumption_finalized"),
    OTHER("other");

    override fun toString(): String = this.value
}
