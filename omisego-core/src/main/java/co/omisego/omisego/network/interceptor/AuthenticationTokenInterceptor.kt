package co.omisego.omisego.network.interceptor

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.HTTPHeaders
import co.omisego.omisego.network.ewallet.AuthenticationHeader
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject

class AuthenticationTokenInterceptor(
    val header: HeaderHandler,
    private val authenticationHeader: AuthenticationHeader
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request()).also {
            if (it.header(HTTPHeaders.AUTHORIZATION) != null) return@also

            // Cannot directly read the body response, since the body can be consumed only once.
            val body = it.peekBody(Long.MAX_VALUE)

            val bufferedResponse = body?.source()?.buffer()?.clone()?.readUtf8()
            val bodyResponse = body?.source()?.readUtf8()

            val rawBody = when {
                // When perform request asynchronously, the raw body was put in the buffer.
                bufferedResponse?.isNotEmpty() == true -> bufferedResponse

                // When perform request synchronously, the raw body was put directly in the body.
                bodyResponse?.isNotEmpty() == true -> bodyResponse

                // Returns when the response was empty.
                else -> return@also
            }

            try {
                val data = JSONObject(rawBody).getJSONObject("data")
                val objectType = data.getString("object")
                if (objectType != "authentication_token") return@also

                val userId = if (data.has("user_id")) data.getString("user_id") else null

                /* Create new authorized header */
                val newHeader = authenticationHeader.create(
                    data.getString("authentication_token"),
                    userId
                )

                /* Replace with authorized header */
                header.setHeader(newHeader)
            } catch (e: JSONException) {
            }
        }
    }
}
