package co.omisego.omisego.network.ewallet

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Endpoints
import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.constant.HTTPHeaders
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.model.BalanceList
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.Setting
import co.omisego.omisego.model.User
import co.omisego.omisego.utils.OMGEncryptionHelper
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldEqual
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.File
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class EWalletClientTest {
    private lateinit var eWalletClient: EWalletClient
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
    private var mockWebServer: MockWebServer = MockWebServer()
    private var mockUrl: HttpUrl = mockWebServer.url("/api/")
    @Rule
    @JvmField
    val expectedEx = ExpectedException.none()!!

    @Before
    fun setUp() {
        val auth = OMGEncryptionHelper.encryptBase64(
                secret.getString("api_key"),
                secret.getString("auth_token")
        )

        eWalletClient = EWalletClient.Builder {
            debugUrl = mockUrl
            authenticationToken = auth
            debug = false
        }.build()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `Calls get_current_user should be match with the expected response`() {
        mockWebServer.enqueue(MockResponse().apply {
            setBody(userFile.readText())
            setResponseCode(200)
        })

        val actualResponse = eWalletClient.eWalletAPI.getCurrentUser().execute().body()!!
        val expectedResponse = buildResponse<User>(userFile.readText())
        actualResponse shouldEqual expectedResponse
    }

    @Test
    fun `Calls get_settings should be match with the expected response`() {
        mockWebServer.enqueue(MockResponse().apply {
            setBody(getSettingFile.readText())
            setResponseCode(200)
        })

        val actualResponse = eWalletClient.eWalletAPI.getSettings().execute().body()!!
        val expectedResponse = buildResponse<Setting>(getSettingFile.readText())
        actualResponse shouldEqual expectedResponse
    }

    @Test
    fun `Calls list_balances should be match with the expected response`() {
        mockWebServer.enqueue(MockResponse().apply {
            setBody(listBalanceFile.readText())
            setResponseCode(200)
        })

        val actualResponse = eWalletClient.eWalletAPI.listBalances().execute().body()!!
        val expectedResponse = buildResponse<BalanceList>(listBalanceFile.readText())
        actualResponse shouldEqual expectedResponse
    }

    @Test
    fun `Empty base_url should throw IllegalStateException`() {
        expectedEx.expect(Exceptions.emptyBaseURL::class.java)
        expectedEx.expectMessage(Exceptions.emptyBaseURL.message)

        EWalletClient.Builder {
            authenticationToken = secret.getString("auth_token")
            debug = false
        }.build()
    }

    @Test
    fun `Empty auth_token should throw IllegalStateException`() {
        expectedEx.expect(Exceptions.emptyAuthenticationToken::class.java)
        expectedEx.expectMessage(Exceptions.emptyAuthenticationToken.message)

        EWalletClient.Builder {
            debugUrl = mockUrl
            debug = false
        }.build()
    }

    @Test
    fun `EWalletClient should be set the header correctly`() {
        mockWebServer.enqueue(MockResponse().apply {
            setBody(userFile.readText())
            setResponseCode(200)
        })
        val expectedAuth = "OMGClient ${OMGEncryptionHelper.encryptBase64(
                secret.getString("api_key"),
                secret.getString("auth_token")
        )}"

        eWalletClient.eWalletAPI.getCurrentUser().execute()
        val request = mockWebServer.takeRequest()
        request.getHeader("Authorization") shouldEqual expectedAuth
        request.getHeader("Accept") shouldEqual HTTPHeaders.ACCEPT_OMG
    }

    @Test
    fun `EWalletClient request to get_current_user with the correct path`() {
        mockWebServer.enqueue(MockResponse().apply {
            setBody(userFile.readText())
            setResponseCode(200)
        })

        eWalletClient.eWalletAPI.getCurrentUser().execute()
        val request = mockWebServer.takeRequest()

        request.path shouldEqual "/api/${Endpoints.GET_CURRENT_USER}"
    }

    @Test
    fun `EWalletClient request to get_setting with the correct path`() {
        mockWebServer.enqueue(MockResponse().apply {
            setBody(getSettingFile.readText())
            setResponseCode(200)
        })

        eWalletClient.eWalletAPI.getSettings().execute()
        val request = mockWebServer.takeRequest()
        request.path shouldEqual "/api/${Endpoints.GET_SETTINGS}"
    }

    @Test
    fun `EWalletClient request to list_balance with the correct path`() {
        mockWebServer.enqueue(MockResponse().apply {
            setBody(listBalanceFile.readText())
            setResponseCode(200)
        })

        eWalletClient.eWalletAPI.listBalances().execute()
        val request = mockWebServer.takeRequest()
        request.path shouldEqual "/api/${Endpoints.LIST_BALANCE}"
    }

    @Test
    fun `EWalletClient request to logout with the correct path`() {
        mockWebServer.enqueue(MockResponse().apply {
            setBody(userFile.readText())
            setResponseCode(200)
        })

        eWalletClient.eWalletAPI.logout().execute()
        val request = mockWebServer.takeRequest()

        request.path shouldEqual "/api/${Endpoints.LOGOUT}"
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

    private inline fun <reified T> buildResponse(responseText: String): OMGResponse<T> {
        val json = JSONObject(responseText)
        val success = json.getBoolean("success")
        val dataText = json.getJSONObject("data").toString()
        val gson = GsonBuilder()
                .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
        val data = gson.fromJson<T>(dataText, T::class.java)
        return OMGResponse(Versions.EWALLET_API, success, data)
    }
}
