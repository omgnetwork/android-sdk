package co.omisego.omisego.websocket.channel.dispatcher.delegator

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketReceive
import co.omisego.omisego.utils.GsonProvider
import com.google.gson.Gson

class SocketReceiveParser(
    /**
     * A gson object for parsing the raw replied json object to the [SocketReceive] object.
     */
    override val gson: Gson = GsonProvider.create()
) : SocketDelegatorContract.PayloadReceiveParser {

    /**
     * Parse a raw json to the [SocketReceive] object.
     *
     * @param json raw json string that being receive from the eWallet web socket API.
     * @return A [SocketReceive]
     */
    override fun parse(json: String): SocketReceive = gson.fromJson(json, SocketReceive::class.java)
}
