package co.omisego.omisego.network.ewallet

import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.custom.retrofit2.adapter.OMGCallAdapterFactory
import co.omisego.omisego.custom.retrofit2.converter.OMGConverterFactory
import co.omisego.omisego.network.InterceptorProvider
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/10/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

/**
 * The class EWalletClient represents an object that knows how to interact with OmiseGO API.
 *
 * Create instances using [EWalletClient.Builder] and pass your implementation of [Callback<T>] interface
 * to generate an implementation
 *
 * For example,
 * <code>
 * val eWalletClient = EWalletClient.Builder {
 *      authenticationToken = YOUR_TOKEN
 *      baseURL = YOUR_BASE_URL
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
     * Set [authenticationToken] and [baseURL] are required before calling [Builder.build].
     * Set [debug] true for printing a log
     *
     * @receiver A [Builder]'s methods.
     */
    class Builder(init: Builder.() -> Unit) {
        var debug: Boolean = false

        /**
         * Set the API [authenticationToken].
         * The [authenticationToken] should be "OMG Base64(api_key:authentication_token)"
         */
        var authenticationToken: String = ""
            set(value) {
                if (value.isEmpty()) throw Exceptions.emptyAuthenticationToken
                field = value
            }

        /**
         * Set the URL of the OmiseGO Wallet API [baseURL].
         */
        var baseURL: String = ""
            set(value) {
                if (value.isEmpty()) throw Exceptions.emptyBaseURL
                field = value
            }


        /**
         * For testing purpose
         */
        internal var debugURL: HttpUrl? = null

        /**
         * Create the [EWalletClient] instance using the configured values.
         * Note: Set [Builder.authenticationToken] and [Builder.baseURL] are required before calling this.
         */
        fun build(): EWalletClient {
            when {
                authenticationToken.isEmpty() -> throw Exceptions.emptyAuthenticationToken
                baseURL.isEmpty() && debugURL == null -> throw Exceptions.emptyBaseURL
            }

            /* Initialize the header by authenticationToken */
            val omgHeader = InterceptorProvider.Header(authenticationToken)

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

            /* Use a simple gson for now */
            val gson = GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create()

            /* Create retrofit with OMGConverter and OMGCallAdapter */
            eWalletClient.retrofit = Retrofit.Builder().apply {
                addConverterFactory(OMGConverterFactory.create(gson))
                addCallAdapterFactory(OMGCallAdapterFactory.create(gson))
                when {
                    debugURL != null -> baseUrl(debugURL!!)
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
