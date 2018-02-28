package co.omisego.omisego.networks

import org.json.JSONObject


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/7/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class RequestOptions {
    private val headers: MutableMap<String, String> by lazy { hashMapOf<String, String>() }
    private val body: JSONObject by lazy { JSONObject() }

    fun setHeaders(vararg pairs: Pair<String, String>) {
        headers.putAll(pairs)
    }

    fun setBody(vararg map: Pair<String, Any>) {
        for ((k, v) in map) {
            body.put(k, v)
        }
    }

    fun getPostBody(): String {
        return body.toString()
    }

    fun getHeader(): Map<String, String> {
        return headers.toMap()
    }
}