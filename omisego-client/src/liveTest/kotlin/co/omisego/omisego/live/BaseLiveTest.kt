package co.omisego.omisego.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.OMGAPIClient
import co.omisego.omisego.model.ClientConfiguration
import co.omisego.omisego.network.ewallet.EWalletClient
import co.omisego.omisego.utils.ResourceFileLoader
import co.omisego.omisego.websocket.OMGSocketClient
import java.util.concurrent.Executor

open class BaseLiveTest : ResourceFileLoader() {
    val secret by lazy { loadSecretFile("secret.json") }

    var config = ClientConfiguration(
        secret.getString("base_url"),
        secret.getString("api_key")
    )
        set(value) {
            socketClient = OMGSocketClient.Builder {
                clientConfiguration = value.copy(baseURL = secret.getString("socket_base_url"))
                executor = Executor { it.run() }
            }.build()
        }

    /* HTTP Client */
    private val eWalletClient by lazy {
        EWalletClient.Builder {
            clientConfiguration = config
        }.build()
    }
    val client by lazy {
        OMGAPIClient(eWalletClient)
    }

    /* Socket Client */
    var socketClient = OMGSocketClient.Builder {
        clientConfiguration = config.copy(baseURL = secret.getString("socket_base_url"))
        executor = Executor { it.run() }
    }.build()
}
