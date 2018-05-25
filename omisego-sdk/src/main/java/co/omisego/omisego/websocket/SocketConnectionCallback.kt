package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 25/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * A web socket connection callback that executed when the web socket client is connected to the server or disconnected from the server.
 */
interface SocketConnectionCallback {
    /**
     * Invoked when the web socket client has connected to the eWallet web socket API successfully.
     */
    fun onConnected()

    /**
     * Invoked when the web socket client has disconnected from the eWallet web socket API.
     *
     * @param throwable (Optional) The exception might be raised if the web socket was not disconnected successfully.
     */
    fun onDisconnected(throwable: Throwable?)
}
