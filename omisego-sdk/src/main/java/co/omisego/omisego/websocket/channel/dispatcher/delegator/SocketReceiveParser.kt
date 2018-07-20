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
import com.google.gson.reflect.TypeToken

/**
 * A data deserializer for receiving an object from the eWallet web socket API
 *
 * @param gson A gson object for parsing the raw replied json object to the [SocketReceive] object.
 */
class SocketReceiveParser(
    override val gson: Gson = GsonProvider.create()
) : SocketDelegatorContract.PayloadReceiveParser {

    /**
     * Parse a raw json to the [SocketReceive] object.
     *
     * @param json raw json string that being receive from the eWallet web socket API.
     * @return A [SocketReceive]
     */
    override fun parse(json: String): SocketReceive<*> {
        return gson.fromJson(json, object : TypeToken<SocketReceive<SocketReceive.SocketData>>() {}.type)
    }
}
