package co.omisego.omisego.websocket.enum

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.OMGEnum
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionFinalizedEvent
import co.omisego.omisego.websocket.event.TransactionConsumptionRequestEvent

enum class SocketEventSend(override val value: String) : OMGEnum {

    /**
     * event used to keep the connection open.
     */
    HEARTBEAT("heartbeat"),

    /**
     * event used to join a channel.
     */
    JOIN("phx_join"),

    /**
     * event used to leave a channel.
     */
    LEAVE("phx_leave");

    override fun toString(): String = this.value
}

enum class SocketSystemEvent(override val value: String) : OMGEnum {
    /**
     * event sent by the server when the client requests to terminate the connection.
     */
    CLOSE("phx_close"),

    /**
     * event sent by the server in case something goes wrong while connecting to a channel for example.
     */
    ERROR("phx_error"),

    /**
     * event sent as a reply to a client-emitted event.
     */
    REPLY("phx_reply");

    override fun toString(): String = this.value
}

enum class SocketCustomEvent(
    override val value: String,
    val eventBuilder: (SocketReceive<*>) -> SocketEvent<*>?
) : OMGEnum {

    TRANSACTION_CONSUMPTION_REQUEST("transaction_consumption_request", eventBuilder(::TransactionConsumptionRequestEvent)),
    TRANSACTION_CONSUMPTION_FINALIZED("transaction_consumption_finalized", eventBuilder(::TransactionConsumptionFinalizedEvent)),
    OTHER("other", { null });

    override fun toString(): String = this.value
}

/**
 * Return a lambda that can build an event of data type T if the type of the response data matches the expected type
 */
@Suppress("UNCHECKED_CAST")
private inline fun <reified T : SocketReceive.SocketData> eventBuilder(
    crossinline eventProvider: (SocketReceive<T>) -> SocketEvent<T>?
): (SocketReceive<*>) -> SocketEvent<T>? = {
    (it as? SocketReceive<T>)?.let { eventProvider(it) }
}
