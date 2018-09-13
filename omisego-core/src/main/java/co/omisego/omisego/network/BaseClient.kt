package co.omisego.omisego.network

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/10/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.constant.HTTPHeaders
import co.omisego.omisego.constant.enums.AuthScheme
import co.omisego.omisego.custom.retrofit2.adapter.OMGCallAdapterFactory
import co.omisego.omisego.custom.retrofit2.converter.OMGConverterFactory
import co.omisego.omisego.custom.retrofit2.executor.MainThreadExecutor
import co.omisego.omisego.model.CredentialConfiguration
import co.omisego.omisego.utils.Base64Encoder
import co.omisego.omisego.utils.GsonProvider
import co.omisego.omisego.utils.OkHttpHelper
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
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
            val config = clientConfiguration ?: throw IllegalStateException(Exceptions.MSG_NULL_CLIENT_CONFIGURATION)
            val omgHeader = okHttpHelper.createHeader(config)

            val interceptors: List<Interceptor> = when (config.authScheme) {
                AuthScheme.ADMIN -> listOf(omgHeader, provideAuthenticationTokenInterceptor(omgHeader))
                AuthScheme.Client -> listOf(omgHeader)
            }

            val client = okHttpHelper.createClient(debug, interceptors, debugOkHttpInterceptors)
            val retrofit = createRetrofit(GsonProvider.create(), client)

            return BaseClient().also {
                it.header = omgHeader
                it.client = client
                it.retrofit = retrofit
            }
        }

        internal fun provideAuthenticationTokenInterceptor(header: HeaderInterceptor): Interceptor {
            return Interceptor { chain ->
                chain.proceed(chain.request()).also {
                    if (it.header(HTTPHeaders.AUTHORIZATION) != null) return@also

                    // Cannot directly read the body response, since the body can be consumed only once.
                    val body = it.peekBody(Long.MAX_VALUE)

                    val bufferedResponse = body?.source()?.buffer()?.clone()?.readUtf8()
                    val bodyResponse = body?.source()?.readUtf8()

                    val rawBody = when {
                        // When perform request asynchronously, the raw body was put in the buffer.
                        bufferedResponse?.isNotEmpty() == true -> bufferedResponse

                        // When perform request synchronously, the raw body was put directly in the body.
                        bodyResponse?.isNotEmpty() == true -> bodyResponse

                        // Returns when the response was empty.
                        else -> return@also
                    }

                    try {
                        val data = JSONObject(rawBody).getJSONObject("data")
                        val objectType = data.getString("object")
                        if (objectType != "authentication_token") return@also

                        /* Create new authorized header */
                        val newHeader = Base64Encoder().encode(
                            data.getString("user_id"),
                            data.getString("authentication_token")
                        )

                        /* Replace with authorized header */
                        header.setHeader(newHeader)
                    } catch (e: JSONException) {
                    }
                }
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
