package co.omisego.omisego.client.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.client.OMGAPIClient
import co.omisego.omisego.client.model.ClientConfiguration
import co.omisego.omisego.client.network.ewallet.EWalletClient
import co.omisego.omisego.client.utils.ResourceFileLoader
import co.omisego.omisego.websocket.OMGSocketClient
import java.util.concurrent.Executor

open class LiveTest : ResourceFileLoader() {
    private val secret by lazy { loadSecretFile("secret.json") }
    private val config by lazy {
        ClientConfiguration(
            secret.getString("base_url"),
            secret.getString("api_key"),
            secret.getString("auth_token")
        )
    }
    private val eWalletClient by lazy {
        EWalletClient.Builder {
            clientConfiguration = config
        }.build()
    }
    val client by lazy {
        OMGAPIClient(eWalletClient)
    }
    val socketClient by lazy {
        OMGSocketClient.Builder {
            clientConfiguration = config.copy(baseURL = secret.getString("socket_base_url"))
            executor = Executor { it.run() }
        }.build()
    }
}
