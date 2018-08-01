package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.CredentialConfiguration
import co.omisego.omisego.network.HeaderInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class OkHttpHelper(
    private val encryption: OMGEncryption = OMGEncryption()
) {
    internal fun createHeader(clientConfiguration: CredentialConfiguration): HeaderInterceptor = HeaderInterceptor(
        clientConfiguration.authScheme,
        encryption.createAuthorizationHeader(clientConfiguration)
    )

    /* Initialize the OKHttpClient with header interceptor*/
    internal fun createClient(
        requiredAuth: Boolean = true,
        debug: Boolean,
        headerInterceptor: HeaderInterceptor,
        debugOkHttpInterceptors: MutableList<Interceptor>
    ) = OkHttpClient.Builder().apply {
        if (requiredAuth)
            addInterceptor(headerInterceptor)

        /* If set debug true, then print the http logging */
        if (debug) {
            for (interceptor in debugOkHttpInterceptors) {
                addNetworkInterceptor(interceptor)
            }
        }
    }.build()
}
