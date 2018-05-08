package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class SocketMessageRef : SocketClientContract.MessageRef {
    override var value: String = "0"
        get() {
            field = "${field.toInt() + 1}"
            return field
        }
}