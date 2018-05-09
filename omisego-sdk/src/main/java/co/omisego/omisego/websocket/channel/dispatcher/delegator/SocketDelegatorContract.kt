package co.omisego.omisego.websocket.channel.dispatcher.delegator

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import com.google.gson.Gson
import okhttp3.Response

interface SocketDelegatorContract {
    interface Core {
        val socketResponseParser: PayloadReceiveParser
        var socketDispatcher: Dispatcher?
    }

    interface PayloadReceiveParser {
        val gson: Gson
        fun parse(json: String): SocketReceive
    }

    interface Dispatcher {
        fun dispatchOnOpened(response: Response)
        fun dispatchOnClosed(code: Int, reason: String)
        fun dispatchOnMessage(response: SocketReceive)
        fun dispatchOnFailure(throwable: Throwable, response: Response?)
    }
}