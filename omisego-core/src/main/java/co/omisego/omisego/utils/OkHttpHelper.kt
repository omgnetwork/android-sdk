package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.CredentialConfiguration
import co.omisego.omisego.network.interceptor.HeaderInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class OkHttpHelper(
    private val encryption: OMGEncryption = OMGEncryption()
) {
    fun createHeaderInterceptor(clientConfiguration: CredentialConfiguration): HeaderInterceptor = HeaderInterceptor(
        clientConfiguration.authScheme,
        encryption.createAuthorizationHeader(clientConfiguration)
    )

    /* Initialize the OKHttpClient with header interceptor*/
    internal fun createClient(
        debug: Boolean,
        interceptors: List<Interceptor>,
        debugOkHttpInterceptors: MutableList<Interceptor>
    ) = OkHttpClient.Builder().apply {
        interceptors.forEach { addInterceptor(it) }

        if (debug) {
            for (interceptor in debugOkHttpInterceptors) {
                addNetworkInterceptor(interceptor)
            }
        }
    }.build()
}
