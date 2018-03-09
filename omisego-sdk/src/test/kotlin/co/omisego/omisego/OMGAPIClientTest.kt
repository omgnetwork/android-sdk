package co.omisego.omisego

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 6/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.ErrorCode
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.custom.Callback
import co.omisego.omisego.custom.retrofit2.adapter.OMGCall
import co.omisego.omisego.model.*
import co.omisego.omisego.network.ewallet.EWalletAPI
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
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.io.File
import java.util.concurrent.Executors

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
    private val errorFile: File by lazy {
        File(javaClass.classLoader.getResource("fail.client-invalid_auth_scheme.json").path)
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

        val callback: Callback<OMGResponse<BalanceList>> = mock()
        omgAPIClient.listBalances().enqueue(callback)

        val expected = Gson().fromJson<OMGResponse<BalanceList>>(result.body(), object : TypeToken<OMGResponse<BalanceList>>() {}.type)

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

        val callback: Callback<OMGResponse<User>> = mock()
        omgAPIClient.getCurrentUser().enqueue(callback)

        val expected = Gson().fromJson<OMGResponse<User>>(result.body(), object : TypeToken<OMGResponse<User>>() {}.type)

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

        val callback: Callback<OMGResponse<Setting>> = mock()
        omgAPIClient.getSettings().enqueue(callback)

        val expected = Gson().fromJson<OMGResponse<Setting>>(result.body(), object : TypeToken<OMGResponse<Setting>>() {}.type)

        Thread.sleep(100)

        verify(callback, times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient should resolve APIError from the EWalletAPI gracefully`() {
        val element = Gson().fromJson(errorFile.readText(), JsonElement::class.java)
        mockWebServer.enqueue(MockResponse().apply {
            setBody(errorFile.readText())
            setResponseCode(200)
        })

        val callback: Callback<OMGResponse<User>> = mock()
        omgAPIClient.getCurrentUser().enqueue(callback)

        val data = element.asJsonObject.get("data")
        val apiError = APIError(ErrorCode.from(data.asJsonObject.get("code").asString), data.asJsonObject.get("description").asString)
        val expected = OMGResponse(Versions.EWALLET_API, false, apiError)

        Thread.sleep(100)
        verify(callback, times(1)).fail(expected)
    }

    @Test
    fun `OMGAPIClient should delegate an error caused by API 500 to APIError successfully`() {
        mockWebServer.enqueue(MockResponse().apply {
            setBody(errorFile.readText())
            setResponseCode(500)
        })

        val callback: Callback<OMGResponse<User>> = mock()
        omgAPIClient.getCurrentUser().enqueue(callback)

        val apiError = APIError(ErrorCode.SERVER_INTERNAL_SERVER_ERROR, "The EWallet API was 500 Internal Server Error")
        val expected = OMGResponse(Versions.EWALLET_API, false, apiError)

        Thread.sleep(100)
        verify(callback, times(1)).fail(expected)
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
