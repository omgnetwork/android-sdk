package co.omisego.omisego

import co.omisego.omisego.networks.core.ewallet.EWalletClient
import co.omisego.omisego.utils.OMGEncryptionHelper
import com.google.gson.JsonElement
import junit.framework.Assert
import kotlinx.coroutines.experimental.delay
import org.json.JSONObject
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlin.test.Test

/**
 * Ripzery
 *
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2018 Ripzery. All rights reserved.
 */

@RunWith(MockitoJUnitRunner::class)
class EWalletClientTest {
    private val secretFileName: String = "secret.json" // Replace your secret file here
    private val secret: JSONObject by lazy { loadSecretFile(secretFileName) }
    private lateinit var baseURL: String
    private lateinit var mAuthtoken: String
    private lateinit var ewallletClient: EWalletClient

    @Before
    fun setUp() {
        baseURL = secret.getString("base_url")
        assertKeyIsNotEmpty()

        val auth = OMGEncryptionHelper.encryptBase64(
                secret.getString("api_key"),
                secret.getString("auth_token")
        )

        ewallletClient = EWalletClient.Builder {
            baseURL = baseURL
            authenticationToken = auth
            debug = false
        }.build()
    }

    @Test
    fun test() {
        ewallletClient.eWalletAPI.listBalance().enqueue(object : Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable?) {
                print("Fail" + t.toString())
            }

            override fun onResponse(call: Call<JsonElement>?, response: Response<JsonElement>) {
                println(response.message())
            }
        })
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

    private fun assertKeyIsNotEmpty() {
        Assert.assertTrue("Assign your baseURL before run the test", baseURL != "")
    }
}