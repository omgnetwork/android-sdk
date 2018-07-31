package co.omisego.omisego.websocket

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/6/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
import okhttp3.WebSocketListener

interface WebSocketListenerProvider {
    val webSocketListener: WebSocketListener
}