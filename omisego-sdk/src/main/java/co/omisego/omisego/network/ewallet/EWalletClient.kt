package co.omisego.omisego.network.ewallet

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/10/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.custom.retrofit2.adapter.OMGCallAdapterFactory
import co.omisego.omisego.custom.retrofit2.converter.OMGConverterFactory
import co.omisego.omisego.custom.retrofit2.executor.MainThreadExecutor
import co.omisego.omisego.model.ClientConfiguration
import co.omisego.omisego.network.InterceptorProvider
import co.omisego.omisego.utils.GsonProvider
import co.omisego.omisego.utils.OMGEncryption
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.Executor

/**
 * The class EWalletClient represents an object that knows how to interact with OmiseGO API.
 *
 * Create instances using [EWalletClient.Builder] and pass your implementation of [OMGCallback<T>] interface
 * to generate an implementation
 *
 * For example,
 * <code>
 * val config = ClientConfiguration(
 *      baseURL = "YOUR_BASE_URL",
 *      apiKey = "YOUR_API_KEY",
 *      authenticationToken = "YOUR_AUTH_TOKEN"
 * )
 * val eWalletClient = EWalletClient.Builder {
 *      clientConfiguration = config
 *      debug = false
 * }.build()
 *
 * </code>
 *
 */
class EWalletClient {
    internal lateinit var eWalletAPI: EWalletAPI
    internal lateinit var header: InterceptorProvider.Header
    internal lateinit var retrofit: Retrofit

    /**
     * Build a new [EWalletClient].
     * Set [apiKey], [authenticationToken] and [baseUrl] are required before calling [Builder.build].
     * Set [debug] true for printing a log
     *
     * @receiver A [Builder]'s methods.
     */
    class Builder(init: Builder.() -> Unit) {
        /**
         * Set the callback executor (default UI thread)
         */
        var callbackExecutor: Executor? = null

        /**
         * (Required) A client configuration that need to be first initialized before calling build()
         */
        var clientConfiguration: ClientConfiguration? = null

        /**
         * (Optional) A boolean indicating if debug info should be printed in the console. Default: false.
         */
        var debug: Boolean = false

        /**
         * The OKHttp interceptor list for debugging purpose.
         */
        var debugOkHttpInterceptors: MutableList<Interceptor> = mutableListOf()

        /**
         * For testing purpose
         */
        internal var debugUrl: HttpUrl? = null

        fun build(): EWalletClient {
            val (baseURL, apiKey, authenticationToken) = clientConfiguration
                ?: throw IllegalStateException(Exceptions.MSG_NULL_CLIENT_CONFIGURATION)

            /* Encrypted base64 of apiKey:authenticationToken */
            val authorizationHeader = OMGEncryption.createAuthorizationHeader(apiKey, authenticationToken)

            /* Initialize the header by authorizationToken */
            val omgHeader = InterceptorProvider.Header(authorizationHeader)

            /* Initialize the EWalletClient and delegate the header from the Builder to EWalletClient */
            val eWalletClient = EWalletClient().apply { header = omgHeader }

            /* Initialize the OKHttpClient with header interceptor*/
            val client: OkHttpClient = OkHttpClient.Builder().apply {
                addInterceptor(omgHeader)

                /* If set debug true, then print the http logging */
                if (debug) {
                    for (interceptor in debugOkHttpInterceptors) {
                        addNetworkInterceptor(interceptor)
                    }
                }
            }.build()

            val gson = GsonProvider.create()

            /* Create retrofit with OMGConverter and OMGCaller */
            eWalletClient.retrofit = Retrofit.Builder().apply {
                addConverterFactory(OMGConverterFactory.create(gson))
                addCallAdapterFactory(OMGCallAdapterFactory.create())
                callbackExecutor(callbackExecutor ?: MainThreadExecutor())
                when {
                    debugUrl != null -> baseUrl(debugUrl!!)
                    else -> baseUrl(baseURL)
                }
                client(client)
            }.build()

            /* Create EWalletAPI client */
            eWalletClient.eWalletAPI = eWalletClient.retrofit.create(EWalletAPI::class.java)
            return eWalletClient
        }

        init {
            init()
        }
    }
}
