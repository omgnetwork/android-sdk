package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.dispatcher.callback.SocketCallbackContract
import okhttp3.Response
import okhttp3.WebSocketListener

class SocketDispatcher(
    override val socketCallback: SocketDispatcherContract.Callback
) : SocketChannelContract.Dispatcher, SocketDispatcherContract.Core, SocketCallbackContract.Dispatcher {
    override fun retrieveWebSocketListener(): WebSocketListener {
        return socketCallback.getWebSocketListener()
    }

    override fun handleRequestEvents() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleConsumeEvents() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dispatchOnOpened(response: Response) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dispatchOnClosed(code: Int, reason: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dispatchOnMessage(response: SocketReceive) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dispatchOnFailure(throwable: Throwable, response: Response?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}