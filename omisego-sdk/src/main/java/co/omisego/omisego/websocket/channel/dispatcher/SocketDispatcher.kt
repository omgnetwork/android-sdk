package co.omisego.omisego.websocket.channel.dispatcher

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.util.Log
import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.websocket.channel.SocketChannelContract
import co.omisego.omisego.websocket.channel.dispatcher.callback.SocketCallbackContract
import co.omisego.omisego.websocket.enum.SocketEventReceive
import okhttp3.Response
import okhttp3.WebSocketListener

class SocketDispatcher(
    override val socketCallback: SocketDispatcherContract.Callback
) : SocketChannelContract.Dispatcher, SocketDispatcherContract.Core, SocketCallbackContract.Dispatcher {
    override var socketChannel: SocketDispatcherContract.SocketChannel? = null
    override fun retrieveWebSocketListener(): WebSocketListener {
        return socketCallback.getWebSocketListener()
    }

    override fun handleRequestEvents() {
        Log.d("SocketDispatcher", "Dispatch On")
    }

    override fun handleConsumeEvents() {
        Log.d("SocketDispatcher", "Dispatch On")
    }

    override fun dispatchOnOpened(response: Response) {
        Log.d("SocketDispatcher", "Dispatch OnOpened")
    }

    override fun dispatchOnClosed(code: Int, reason: String) {
        Log.d("SocketDispatcher", "Dispatch OnClosed")
    }

    override fun dispatchOnMessage(response: SocketReceive) {
        Log.d("SocketDispatcher", "Dispatch OnMessage $response")
        when (response.event) {
            SocketEventReceive.CLOSE -> socketChannel?.onLeftChannel(response.topic)
            SocketEventReceive.REPLY ->
                if (socketChannel?.joined(response.topic) != true)
                    socketChannel?.onJoinedChannel(response.topic)
            SocketEventReceive.ERROR -> TODO()
            SocketEventReceive.TRANSACTION_CONSUMPTION_REQUEST -> TODO()
            SocketEventReceive.TRANSACTION_CONSUMPTION_FINALIZED -> TODO()
            SocketEventReceive.OTHER -> TODO()
        }
    }

    override fun dispatchOnFailure(throwable: Throwable, response: Response?) {
        Log.d("SocketDispatcher", "Dispatch OnFailure")
    }
}