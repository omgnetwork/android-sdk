package co.omisego.omisego

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 6/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.custom.Callback
import co.omisego.omisego.custom.Serializer
import co.omisego.omisego.model.BalanceList
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.Setting
import co.omisego.omisego.model.User
import co.omisego.omisego.network.ewallet.EWalletClient
import co.omisego.omisego.utils.OMGEncryptionHelper
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.mock
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.File

class OMGAPIClientTest {
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
    private lateinit var eWalletClient: EWalletClient
    private lateinit var mockWebServer: MockWebServer
    private lateinit var mockUrl: HttpUrl

    private lateinit var omgAPIClient: OMGAPIClient

    @Before
    fun setup() {
        val auth = OMGEncryptionHelper.encryptBase64(
                secret.getString("api_key"),
                secret.getString("auth_token")
        )

        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockUrl = mockWebServer.url("/api/")

        eWalletClient = EWalletClient.Builder {
            debugURL = mockUrl
            authenticationToken = auth
            debug = false
        }.build()

        omgAPIClient = OMGAPIClient(eWalletClient)
    }

    @Test
    fun `OMGAPIClient should call list_balance and success`() {
        val element = Gson().fromJson(listBalanceFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        mockWebServer.enqueue(MockResponse().apply {
            setBody(listBalanceFile.readText())
            setResponseCode(200)
        })

        val callback: Callback<BalanceList> = mock()
        omgAPIClient.listBalances(callback)

        val expected = Serializer().success<OMGResponse<BalanceList>>(result, object : TypeToken<OMGResponse<BalanceList>>() {}.type)

        Thread.sleep(100)

        verify(callback, times(1)).success(expected)
    }


    @Test
    fun `OMGAPIClient should call get_current_user and success`() {
        val element = Gson().fromJson(userFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        mockWebServer.enqueue(MockResponse().apply {
            setBody(userFile.readText())
            setResponseCode(200)
        })

        val callback: Callback<User> = mock()
        omgAPIClient.getCurrentUser(callback)

        val expected = Serializer().success<OMGResponse<User>>(result, object : TypeToken<OMGResponse<User>>() {}.type)

        Thread.sleep(100)

        verify(callback, times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient should call get_setting and success`() {
        val element = Gson().fromJson(getSettingFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        mockWebServer.enqueue(MockResponse().apply {
            setBody(getSettingFile.readText())
            setResponseCode(200)
        })

        val callback: Callback<Setting> = mock()
        omgAPIClient.getSettings(callback)

        val expected = Serializer().success<OMGResponse<Setting>>(result, object : TypeToken<OMGResponse<Setting>>() {}.type)

        Thread.sleep(100)

        verify(callback, times(1)).success(expected)
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
