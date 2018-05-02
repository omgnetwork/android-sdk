package co.omisego.omisego.websocket

import android.util.Log
import okhttp3.*
import okio.ByteString

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 30/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class WebsocketClient {
    private lateinit var mSocketClient: WebSocket
    private lateinit var mWebsocketCallback: WebsocketCallback
    fun init() {
        val request = Request.Builder().url("ws://192.168.1.16:8080").build()
        val client = OkHttpClient()
        mSocketClient = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket?, response: Response?) {
                Log.d("EuroTag", "onOpen")
            }

            override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
                Log.d("EuroTag", "onFailure")
            }

            override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
                Log.d("EuroTag", "onClosing")
            }

            override fun onMessage(webSocket: WebSocket?, text: String?) {
                Log.d("EuroTag", "onMessage text $text")

                mWebsocketCallback.onMessage(text!!)
            }

            override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
                Log.d("EuroTag", "onMessage byte")
            }

            override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
                Log.d("EuroTag", "onClosed")
            }
        })


//        client.dispatcher().executorService().shutdown()
    }

    fun send(msg: String){
        mSocketClient.send(msg)
    }

    fun setWebSocketCallback(callback: WebsocketCallback){
        mWebsocketCallback = callback
    }

    interface WebsocketCallback {
        fun onMessage(msg: String)
    }
}