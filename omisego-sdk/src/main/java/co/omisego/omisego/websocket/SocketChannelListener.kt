package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 25/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError

interface SocketChannelListener {
    /**
     * Invoked when the client joined the channel successfully.
     *
     * @param topic A topic indicating which channel will be joined.
     * @return true if the event was consumed, false otherwise
     */
    fun onJoinedChannel(topic: String): Boolean

    /**
     * Invoked when the client left the channel successfully.
     *
     * @param topic A topic indicating which channel will be joined.
     * @return true if the event was consumed, false otherwise
     */
    fun onLeftChannel(topic: String): Boolean

    /**
     * Invoked when something goes wrong while connecting to a channel.
     *
     * @param apiError An [APIError] instance for explaining the failure reason.
     * @return true if the event was consumed, false otherwise
     */
    fun onError(apiError: APIError): Boolean
}
