package co.omisego.omisego

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.AdminConfiguration
import co.omisego.omisego.network.ewallet.EWalletAdmin
import co.omisego.omisego.utils.ResourceFileLoader

open class BaseLiveTest : ResourceFileLoader() {
    val secret by lazy { loadSecretFile("secret.json") }
    private val config by lazy {
        AdminConfiguration(
            secret.getString("base_url")
        )
    }
    private val eWalletAdmin by lazy {
        EWalletAdmin.Builder {
            clientConfiguration = config
        }.build()
    }
    val client by lazy {
        OMGAPIAdmin(eWalletAdmin)
    }
}
