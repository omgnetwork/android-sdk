package co.omisego.omisego.websocket.enum

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

enum class SocketStatusCode(val code: Int) {
    NORMAL(1000),
    CONNECTION_FAILURE(1001)
}
