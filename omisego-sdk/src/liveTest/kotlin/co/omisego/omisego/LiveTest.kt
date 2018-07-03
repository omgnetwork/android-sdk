package co.omisego.omisego

import co.omisego.omisego.model.ClientConfiguration
import co.omisego.omisego.network.ewallet.EWalletClient
import co.omisego.omisego.utils.ResourceFileLoader
import org.junit.Ignore
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

@Ignore("This live tests need to be tested manually in your local machine.")
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
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
}
