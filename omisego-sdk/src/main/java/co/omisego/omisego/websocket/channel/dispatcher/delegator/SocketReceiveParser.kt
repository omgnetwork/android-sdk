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

class SocketReceiveParser(override val gson: Gson = GsonProvider.create()) : SocketDelegatorContract.PayloadReceiveParser {
    override fun parse(json: String): SocketReceive = gson.fromJson(json, SocketReceive::class.java)
}