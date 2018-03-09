package co.omisego.omisego.network

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.HTTPHeaders
import okhttp3.Interceptor
import okhttp3.Response

class InterceptorProvider {
    class Header(private var authenticationToken: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder().apply {
                addHeader(HTTPHeaders.AUTHORIZATION, "${HTTPHeaders.AUTHORIZATION_SCHEME} $authenticationToken")
                addHeader(HTTPHeaders.ACCEPT, HTTPHeaders.ACCEPT_OMG)
            }.build()
            return chain.proceed(newRequest)
        }

        fun setHeader(token: String) {
            authenticationToken = token
        }
    }
}
