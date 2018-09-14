package co.omisego.omisego.network.interceptor

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.HTTPHeaders
import co.omisego.omisego.constant.enums.AuthScheme
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(
    internal val authScheme: AuthScheme,
    internal var authenticationToken: String
) : Interceptor, HeaderHandler {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder().apply {
            if (authenticationToken.isNotEmpty())
                addHeader(HTTPHeaders.AUTHORIZATION, "$authScheme $authenticationToken")
            addHeader(HTTPHeaders.ACCEPT, HTTPHeaders.ACCEPT_OMG)
        }.build()
        return chain.proceed(newRequest)
    }

    override fun setHeader(authenticationToken: String) {
        this.authenticationToken = authenticationToken
    }
}
