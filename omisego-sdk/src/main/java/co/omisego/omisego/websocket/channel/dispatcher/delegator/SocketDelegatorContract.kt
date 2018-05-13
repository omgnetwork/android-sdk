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
    /* Delegator Package */
    interface Delegator {
        /**
         * A socketResponseParser is responsible for parse a raw replied json object to the [SocketReceive] model.
         */
        val socketResponseParser: PayloadReceiveParser

        /**
         * A socketDispatcher is responsible for the further handling the raw response from the OkHttp's [WebSocketListener].
         */
        var socketDispatcher: Dispatcher?
    }

    interface PayloadReceiveParser {
        /**
         * A gson object for parsing the raw replied json object to the [SocketReceive] object.
         */
        val gson: Gson

        /**
         * Parse a raw json to the [SocketReceive] object.
         *
         * @param json raw json string that being receive from the eWallet web socket API.
         * @return A [SocketReceive]
         */
        fun parse(json: String): SocketReceive
    }

    /* Dispatcher Package */
    interface Dispatcher {
        /**
         * Invoked when the method [WebSocketListener]'s onOpen is called.
         *
         * @param response The response from the OkHttp's WebSocket.
         */
        fun dispatchOnOpened(response: Response)

        /**
         * Invoked when the method [WebSocketListener]'s onClosed is called.
         *
         * @param code the status code explaining why the connection is being closed.
         * @param reason A human-readable string explaining why the connection is closing.
         */
        fun dispatchOnClosed(code: Int, reason: String)

        /**
         * Invoked when the method [WebSocketListener]'s onMessage is called.
         *
         * @param response A [SocketReceive] object to be used for further handling by the [Dispatcher]
         */
        fun dispatchOnMessage(response: SocketReceive)

        /**
         * Invoked when the method [WebSocketListener]'s onFailure is called.
         *
         * @param throwable An exception is delegated from the [WebSocketListener]'s onFailure
         * @param response A response is delegated by the [WebSocketListener]'s onFailure
         */
        fun dispatchOnFailure(throwable: Throwable, response: Response?)
    }
}
