package co.omisego.omisego.network

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
import co.omisego.omisego.model.CredentialConfiguration
import co.omisego.omisego.network.ewallet.AuthenticationHeader
import co.omisego.omisego.network.interceptor.AuthenticationTokenInterceptor
import co.omisego.omisego.network.interceptor.HeaderInterceptor
import co.omisego.omisego.utils.GsonProvider
import co.omisego.omisego.utils.OkHttpHelper
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.Interceptor
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
open class BaseClient {
    lateinit var header: HeaderInterceptor
    lateinit var retrofit: Retrofit
    lateinit var client: OkHttpClient

    abstract class Builder(val init: Builder.() -> Unit) {
        /**
         * Set the callback executor (default UI thread)
         */
        var callbackExecutor: Executor = MainThreadExecutor()

        /**
         * (Required) A client configuration that need to be first initialized before calling build()
         */
        var clientConfiguration: CredentialConfiguration? = null

        /**
         * (Optional) A boolean indicating if debug info should be printed in the console. Default: false.
         */
        var debug: Boolean = false

        /**
         * A authentication header creator.
         * It will be used by the [AuthenticationTokenInterceptor] to set a new [AuthenticationHeader] when login successfully.
         *
         * This property should not be set by the user, it should be set automatically by the sdk when initialize the EWalletClient or EWalletAdmin.
         */
        abstract var authenticationHeader: AuthenticationHeader

        /**
         * The OKHttp interceptor list for debugging purpose.
         */
        var debugOkHttpInterceptors: MutableList<Interceptor> = mutableListOf(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )

        /**
         * For testing purpose
         */
        var debugUrl: HttpUrl? = null

        private val okHttpHelper: OkHttpHelper by lazy { OkHttpHelper() }

        open fun build(): BaseClient {
            /* Verify if the [CredentialConfiguration] is initialized correctly */
            val config = clientConfiguration ?: throw IllegalStateException(Exceptions.MSG_NULL_CLIENT_CONFIGURATION)

            /* For inject the authentication header for each request*/
            val headerInterceptor = okHttpHelper.createHeaderInterceptor(config)

            /* For set new authentication header to headerInterceptor automatically when login successfully */
            val authenticationTokenInterceptor = AuthenticationTokenInterceptor(
                headerInterceptor,
                authenticationHeader
            )

            /* Collect all interceptors */
            val interceptors: List<Interceptor> = listOf(
                headerInterceptor,
                authenticationTokenInterceptor
            )

            val client = okHttpHelper.createClient(debug, interceptors, debugOkHttpInterceptors)
            val retrofit = createRetrofit(GsonProvider.create(), client)

            return BaseClient().also {
                it.header = headerInterceptor
                it.client = client
                it.retrofit = retrofit
            }
        }

        internal fun createRetrofit(gson: Gson, client: OkHttpClient) = Retrofit.Builder().apply {
            addConverterFactory(OMGConverterFactory.create(gson))
            addCallAdapterFactory(OMGCallAdapterFactory.create())
            callbackExecutor(callbackExecutor)
            when {
                debugUrl != null -> baseUrl(debugUrl!!)
                else -> baseUrl(clientConfiguration!!.baseURL)
            }
            client(client)
        }.build()

        init {
            init()
        }
    }
}
