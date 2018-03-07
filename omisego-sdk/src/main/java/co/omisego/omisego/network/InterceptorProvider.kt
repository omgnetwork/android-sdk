package co.omisego.omisego.network

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Headers
import okhttp3.Interceptor
import okhttp3.Response

class InterceptorProvider {
    class Header(private val authenticationToken: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder().apply {
                addHeader(Headers.AUTHORIZATION, "${Headers.AUTHORIZATION_SCHEME} $authenticationToken")
                addHeader(Headers.ACCEPT, Headers.ACCEPT_OMG)
            }.build()
            return chain.proceed(newRequest)
        }
    }
}