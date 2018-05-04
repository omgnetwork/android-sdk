package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.socket.SocketSend
import co.omisego.omisego.utils.GsonProvider
import com.google.gson.Gson

class SocketSendParser(override val gson: Gson = GsonProvider.create()) : SocketClientContract.PayloadSendParser {
    override fun parse(payload: SocketSend): String = gson.toJson(payload)
}