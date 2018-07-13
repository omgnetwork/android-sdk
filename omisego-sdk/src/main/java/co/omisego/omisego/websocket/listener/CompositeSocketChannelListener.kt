package co.omisego.omisego.websocket.listener

/**
 * OmiseGO
 *
 * Created by Yannick Badoual on 7/13/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError

class CompositeSocketChannelListener(
    private val listeners: MutableSet<SocketChannelListener> = linkedSetOf()
) : SocketChannelListener, MutableSet<SocketChannelListener> by listeners {

    override fun onJoinedChannel(topic: String) = any { it.onJoinedChannel(topic) }

    override fun onLeftChannel(topic: String) = any { it.onLeftChannel(topic) }

    override fun onError(apiError: APIError) = any { it.onError(apiError) }
}
