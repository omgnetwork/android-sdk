package co.omisego.omisego.network

import co.omisego.omisego.constant.Headers
import okhttp3.Interceptor
import okhttp3.Response


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class InterceptorProvider {
    class Header(private val authenticationToken: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder().apply {
                addHeader(Headers.HEADER_AUTHORIZATION, "${Headers.HEADER_AUTHORIZATION_SCHEME} $authenticationToken")
                addHeader(Headers.HEADER_ACCEPT, Headers.HEADER_ACCEPT_OMG_TYPE)
            }.build()
            return chain.proceed(newRequest)
        }
    }
}