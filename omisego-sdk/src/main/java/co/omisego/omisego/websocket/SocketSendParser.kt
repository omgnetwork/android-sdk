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

/**
 * A data serializer for sending an object to the eWallet web socket API
 *
 * @param gson Thr gson object for parsing the [SocketSend] to the json string.
 */
class SocketSendParser(
    override val gson: Gson = GsonProvider.create()
) : SocketClientContract.PayloadSendParser {

    /**
     * Parse [SocketSend] object to raw json for sending to the web socket API.
     *
     * @param payload [SocketSend] object which will be sent to the eWallet web socket API.
     * @return A json string for sending to the web socket API.
     */
    override fun parse(payload: SocketSend): String = gson.toJson(payload)
}
