package co.omisego.omisego.network.ewallet

import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.network.InterceptorProvider
import co.omisego.omisego.custom.JsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/10/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class EWalletClient {
    lateinit var eWalletAPI: EWalletAPI

    class Builder(init: Builder.() -> Unit) {
        var debug: Boolean = false
        var authenticationToken: String = ""
            set(value) {
                if (value.isEmpty()) throw Exceptions.emptyAuthenticationToken
                field = value
            }
        var baseURL: String = ""
            set(value) {
                if (value.isEmpty()) throw Exceptions.emptyBaseURL
                field = value
            }

        fun build(): EWalletClient {
            when {
                authenticationToken.isEmpty() -> throw Exceptions.emptyAuthenticationToken
                baseURL.isEmpty() -> throw Exceptions.emptyBaseURL
            }

            val eWalletClient = EWalletClient()
            val client: OkHttpClient = OkHttpClient.Builder().apply {
                addInterceptor(InterceptorProvider.Header(authenticationToken))
                if (debug) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }.build()

            val retrofit = Retrofit.Builder()
                    .addConverterFactory(JsonConverterFactory())
                    .baseUrl(baseURL)
                    .client(client)
                    .build()

            eWalletClient.eWalletAPI = retrofit.create(EWalletAPI::class.java)
            return eWalletClient
        }

        init {
            init()
        }
    }
}
