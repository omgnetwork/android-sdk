package co.omisego.omisego

import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.model.Balance
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.Setting
import co.omisego.omisego.model.User
import co.omisego.omisego.network.ewallet.EWalletClient
import co.omisego.omisego.utils.OMGEncryptionHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import junit.framework.Assert
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldEqual
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.ExpectedException
import java.io.File
import kotlin.test.Test


/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2018 OmiseGO. All rights reserved.
 */

//@RunWith(MockitoJUnitRunner::class)
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
    private lateinit var mockWebServer: MockWebServer
    @Rule
    @JvmField
    val expectedEx = ExpectedException.none()!!

    @Before
    fun setUp() {
        assertKeyIsNotEmpty()

        val auth = OMGEncryptionHelper.encryptBase64(
                secret.getString("api_key"),
                secret.getString("auth_token")
        )

        eWalletClient = EWalletClient.Builder {
            baseURL = secret.getString("base_url")
            authenticationToken = auth
            debug = false
        }.build()

        println("Start")

        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockWebServer.url(secret.getString("base_url"))
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

        val response = eWalletClient.eWalletAPI.getCurrentUser().execute()
        val actualResponse = buildResponse<User>(response.body().toString())
        val expectedResponse = buildResponse<User>(userFile.readText())
        println(actualResponse)
        actualResponse shouldEqual expectedResponse
    }

    @Test
    fun `Calls get_settings should be match with the expected response`() {
        mockWebServer.enqueue(MockResponse().apply {
            setBody(getSettingFile.readText())
            setResponseCode(200)
        })

        val response = eWalletClient.eWalletAPI.getSettings().execute()
        val expectedResponse = buildResponse<Setting>(response.body().toString())
        println(expectedResponse)
        val actualResponse = buildResponse<Setting>(getSettingFile.readText())
        actualResponse shouldEqual expectedResponse
    }

    @Test
    fun `Calls list_balances should be match with the expected response`() {
        mockWebServer.enqueue(MockResponse().apply {
            setBody(listBalanceFile.readText())
            setResponseCode(200)
        })

        val response = eWalletClient.eWalletAPI.listBalance().execute()
        val expectedResponse = buildResponse<List<Balance>>(response.body().toString())

        println(expectedResponse)

        val actualResponse = buildResponse<List<Balance>>(listBalanceFile.readText())
        actualResponse shouldEqual expectedResponse
    }

    // TODO:
    // 1. empty base url should throw illegal state
    // 2. empty authentication token should throw illegal state
    // 3. Check header should be correct
    // 4. Check base url should be correct
    // 5. Check Ewallet API should be correct

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
            baseURL = secret.getString("base_url")
            debug = false
        }.build()
    }

    @Test
    fun `EWalletClient should be set token header correctly`() {
        mockWebServer.enqueue(MockResponse().apply {
            setBody(userFile.readText())
            setResponseCode(200)
        })
        eWalletClient.eWalletAPI.getCurrentUser().execute()
//        val request = mockWebServer.takeRequest()

//        val request = mockWebServer.takeRequest(3, TimeUnit.SECONDS)
//        println(request.requestLine + request.getHeader("Authorization"))
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

    private fun <T> buildResponse(responseText: String): OMGResponse<T> {
        val token = object : TypeToken<OMGResponse<T>>() {}.type
        return Gson().fromJson<OMGResponse<T>>(responseText, token)
    }

    private fun assertKeyIsNotEmpty() {
        Assert.assertTrue("Assign your baseURL before run the test", secret.getString("base_url") != "")
    }
}