package co.omisego.omisego

import co.omisego.omisego.custom.Callback
import co.omisego.omisego.model.ApiError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.Setting
import co.omisego.omisego.network.ewallet.EWalletClient
import org.json.JSONObject
import org.junit.Test
import java.io.File

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 6/3/2018 AD.
 * Copyright © 2018 OmiseGO. All rights reserved.
 */
class NewOMGAPIClientTest {
    private val secretFileName: String = "secret.json" // Replace your secret file here
    private val secret: JSONObject by lazy { loadSecretFile(secretFileName) }
    private val userFile: File by lazy {
        File(javaClass.classLoader.getResource("user.me-post.json").path)
    }
    private val listBalanceFile: File by lazy {
        File(javaClass.classLoader.getResource("me.list_balances-post.json").path)
    }
    private val getSettingFile: File by lazy {
        File(javaClass.classLoader.getResource("me.get_settings-post.json").path)
    }

    @Test
    fun `OMGAPIClient should call get_setting and success`() {
        val eWalletClient = EWalletClient.Builder {
            baseURL = secret.getString("base_url")
            authenticationToken = secret.getString("auth_token")
            debug = false
        }.build()

        val omgAPIClient = NewOMGAPIClient(eWalletClient)

//        omgAPIClient.getSettings(object : Callback<Setting> {
//            override fun success(response: OMGResponse<Setting>) {
//                println(response)
//            }
//
//            override fun fail(response: OMGResponse<ApiError>) {
//                println(response)
//            }
//
//        })
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