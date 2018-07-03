package co.omisego.omisego

import co.omisego.omisego.model.ClientConfiguration
import co.omisego.omisego.network.ewallet.EWalletClient
import org.amshove.kluent.shouldNotBe
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class OMGAPIClientIntegrationTest {
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

    private val client by lazy {
        OMGAPIClient(eWalletClient)
    }

    @Test
    fun `test get_current_user`() {
        val user = client.getCurrentUser().execute()
        user.body() shouldNotBe null
        with(user.body()!!.data) {
            this.id
        }
    }

    private fun loadSecretFile(filename: String): JSONObject {
        val resourceUserURL = javaClass.classLoader.getResource(filename) // This is invisible because it's stored in local ("secret").

        return try {
            val secretFile = File(resourceUserURL.path)
            JSONObject(secretFile.readText())
        } catch (e: IllegalStateException) {
            throw IllegalStateException("Please create the file $filename. See the file secret.example.json for the reference.")
        }
    }
}