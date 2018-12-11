package co.omisego.omisego.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.OMGAPIAdmin
import co.omisego.omisego.model.AdminConfiguration
import co.omisego.omisego.network.ewallet.EWalletAdmin
import co.omisego.omisego.utils.ResourceFileLoader
import okhttp3.logging.HttpLoggingInterceptor

open class BaseLiveTest : ResourceFileLoader() {
    val secret by lazy { loadSecretFile("secret.json") }
    val config by lazy {
        AdminConfiguration(
            secret.getString("base_url")
        )
    }

    /* object to be used for create HTTP request client */
    private val eWalletAdmin by lazy {
        EWalletAdmin.Builder {
            clientConfiguration = config
            debug = true
            debugOkHttpInterceptors = mutableListOf(
                HttpLoggingInterceptor {
                    println(it)
                }.setLevel(HttpLoggingInterceptor.Level.BODY)
            )
        }.build()
    }

    /* HTTP request client */
    val client by lazy {
        OMGAPIAdmin(eWalletAdmin)
    }
}
