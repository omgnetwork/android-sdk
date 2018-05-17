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
import co.omisego.omisego.network.InterceptorProvider
import co.omisego.omisego.utils.GsonProvider
import co.omisego.omisego.utils.OMGEncryption
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
 * val eWalletClient = EWalletClient.Builder {
 *      authorizationToken = YOUR_TOKEN
 *      apiKey = YOUR_API_KEY
 *      baseUrl = YOUR_BASE_URL
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
        var debug: Boolean = false

        /**
         * Set the [authenticationToken].to be a part of the authorizationToken in the http request header
         */
        var authenticationToken: String = ""
            set(value) {
                if (value.isEmpty()) throw IllegalStateException(Exceptions.MSG_EMPTY_AUTH_TOKEN)
                field = value
            }

        /**
         * Set the [apiKey].to be a part of the authorizationToken in the http request header
         */
        var apiKey: String = ""
            set(value) {
                if (value.isEmpty()) throw IllegalStateException(Exceptions.MSG_EMPTY_API_KEY)
                field = value
            }

        /**
         * Set the URL of the OmiseGO Wallet API [baseUrl].
         */
        var baseUrl: String = ""
            set(value) {
                if (value.isEmpty()) throw IllegalStateException(Exceptions.MSG_EMPTY_BASE_URL)
                field = value
            }

        /**
         * Set the callback executor (default UI thread)
         */
        var callbackExecutor: Executor? = null

        /**
         * For testing purpose
         */
        internal var debugUrl: HttpUrl? = null

        /**
         * Create the [EWalletClient] instance using the configured values.
         * Note: Set [Builder.authenticationToken], [Builder.apiKey] and [Builder.baseUrl] are required before calling this.
         */
        fun build(): EWalletClient {
            when {
                authenticationToken.isEmpty() -> throw IllegalStateException(Exceptions.MSG_EMPTY_AUTH_TOKEN)
                apiKey.isEmpty() -> throw IllegalStateException(Exceptions.MSG_EMPTY_API_KEY)
                baseUrl.isEmpty() && debugUrl == null -> throw IllegalStateException(Exceptions.MSG_EMPTY_BASE_URL)
            }

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
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
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
                    else -> baseUrl(this@Builder.baseUrl)
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
