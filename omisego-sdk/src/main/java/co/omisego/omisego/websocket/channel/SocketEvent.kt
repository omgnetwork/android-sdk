package co.omisego.omisego.websocket.channel

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

enum class SocketEventSend(val value: String) {
    HEARTBEAT("heartbeat"),
    JOIN("phx_join"),
    LEAVE("phx_leave");

    override fun toString(): String = this.value

    companion object {
        /**
         * Convert an error code string to [SocketEventSend]
         *
         * @param value Socket event value
         * @return SocketEventSend the socket event.
         */
        fun from(value: String): SocketEventSend {
            return SocketEventSend.valueOf(value)
        }
    }
}

enum class SocketEvent(val value: String) {
    REPLY("phx_reply"),
    ERROR("phx_error"),
    CLOSE("phx_close"),
    TRANSACTION_CONSUMPTION_REQUEST("transaction_consumption_request"),
    TRANSACTION_CONSUMPTION_FINALIZED("transaction_consumption_finalized"),
    OTHER("other");

    override fun toString(): String = this.value

    companion object {
        /**
         * Convert an error code string to [SocketEvent]
         *
         * @param value Socket event value
         * @return SocketEvent the socket event.
         */
        fun from(value: String): SocketEvent {
            return SocketEvent.valueOf(value)
        }
    }
}