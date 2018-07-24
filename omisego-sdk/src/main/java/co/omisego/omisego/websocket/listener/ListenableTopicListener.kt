package co.omisego.omisego.websocket.listener

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 21/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.operation.Listenable
import co.omisego.omisego.websocket.event.SocketEvent
import co.omisego.omisego.websocket.strategy.FilterStrategy

abstract class ListenableTopicListener(
    listenable: Listenable
) : SimpleSocketCustomEventListener<SocketEvent<*>>() {
    final override val strategy: FilterStrategy = FilterStrategy.Topic(listenable.socketTopic)
}
