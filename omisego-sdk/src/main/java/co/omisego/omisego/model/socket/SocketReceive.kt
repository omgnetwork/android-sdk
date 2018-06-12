package co.omisego.omisego.model.socket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.utils.Either
import co.omisego.omisego.websocket.enum.SocketCustomEvent
import co.omisego.omisego.websocket.enum.SocketSystemEvent

/**
 * A model for receiving the replied object from the eWallet web socket API.
 * It closely follows the usual envelope used in the eWallet HTTP APIs with some added attributes related to web socket.
 * Note that those events can either be successful or not.
 *
 * For example, when sending the [SocketCustomEvent.TRANSACTION_CONSUMPTION_FINALIZED],
 * it is possible to receive the finalized consumption OR an error stating that it was finalized in a failed state because there were not enough funds for the actual transaction to proceed.
 *
 * @param topic The topic (channel) to which the event was sent (probably the name of the channel you joined).
 * @param event The event could be either [SocketSystemEvent] or [SocketCustomEvent].
 * @param ref null for events emitted from the server in response to a server action.
 * @param data The data relevant to the event.
 * Could be null if success field is equal to false (but could also contain the [SocketReceive.Data] to provide context for the error).
 * @param version The web socket API version.
 * @param success Defines if the event is the result of a successful action or not.
 * @param error The error resulting from the action generating the event
 */
data class SocketReceive(
    val topic: String,
    val event: Either<SocketSystemEvent, SocketCustomEvent>,
    val ref: String? = null,
    val data: SocketData? = null,
    val version: String,
    val success: Boolean,
    val error: APIError? = null
) {
    interface SocketData
    data class Other(val data: Map<String, Any>) : SocketData
}
