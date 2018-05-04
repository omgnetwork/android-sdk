package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.websocket.channel.SocketChannelContract
import okhttp3.WebSocketListener

class SocketDispatcher(
    override val socketCallback: SocketDispatcherContract.Callback
) : SocketChannelContract.Dispatcher, SocketDispatcherContract.Core {
    override fun retrieveWebSocketListener(): WebSocketListener {
        return socketCallback.getWebSocketListener()
    }

    override fun handleRequestEvents() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleConsumeEvents() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}