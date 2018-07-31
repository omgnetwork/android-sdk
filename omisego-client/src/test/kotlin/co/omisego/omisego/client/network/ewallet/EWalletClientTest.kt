package co.omisego.omisego.client.network.ewallet

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.client.constant.Endpoints
import co.omisego.omisego.client.extension.mockEnqueueWithHttpCode
import co.omisego.omisego.client.model.ClientConfiguration
import co.omisego.omisego.client.util.Encryptor
import co.omisego.omisego.client.util.GsonDelegator
import co.omisego.omisego.client.util.ResourceFile
import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.constant.HTTPHeaders
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.Setting
import co.omisego.omisego.model.User
import co.omisego.omisego.model.WalletList
import co.omisego.omisego.model.transaction.list.TransactionListParams
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.concurrent.Executor
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class EWalletClientTest : GsonDelegator() {
    private val userFile: File by ResourceFile("user.json")
    private val getWalletsFile: File by ResourceFile("get_wallets.json")
    private val getTransactionsFile: File by ResourceFile("get_transactions.json")
    private val getSettingFile: File by ResourceFile("setting.json")
    private var mockWebServer: MockWebServer = MockWebServer()
    private var mockUrl: HttpUrl = mockWebServer.url("/api/client/")
    private val mockInterceptors = mutableListOf<Interceptor>(
        mock(),
        mock(),
        mock()
    )
    private lateinit var eWalletClient: EWalletClient
    private lateinit var config: ClientConfiguration

    @Before
    fun setUp() {
        config = ClientConfiguration(
            "base_url",
            "api_key",
            "auth_token"
        )

        eWalletClient = EWalletClient.Builder {
            debugUrl = mockUrl
            clientConfiguration = config
            callbackExecutor = Executor { it.run() }
        }.build()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `calls get_current_user should be match with the expected response`() {
        userFile.mockEnqueueWithHttpCode(mockWebServer)

        val actualResponse = eWalletClient.eWalletAPI.getCurrentUser().execute().body()!!
        val expectedResponse = buildResponse<User>(userFile.readText())
        actualResponse shouldEqual expectedResponse
    }

    @Test
    fun `calls get_settings should be match with the expected response`() {
        getSettingFile.mockEnqueueWithHttpCode(mockWebServer)

        val actualResponse = eWalletClient.eWalletAPI.getSettings().execute().body()!!
        val expectedResponse = buildResponse<Setting>(getSettingFile.readText())
        actualResponse shouldEqual expectedResponse
    }

    @Test
    fun `calls list_wallets should be match with the expected response`() {
        getWalletsFile.mockEnqueueWithHttpCode(mockWebServer)

        val actualResponse = eWalletClient.eWalletAPI.getWallets().execute().body()!!
        val expectedResponse = buildResponse<WalletList>(getWalletsFile.readText())
        actualResponse shouldEqual expectedResponse
    }

    @Test
    fun `should be set the header correctly`() {
        userFile.mockEnqueueWithHttpCode(mockWebServer)
        val expectedAuth = "OMGClient ${Encryptor.encryptBase64(
            "api_key",
            "auth_token"
        )}"

        eWalletClient.eWalletAPI.getCurrentUser().execute()
        val request = mockWebServer.takeRequest()
        request.getHeader("Authorization") shouldEqual expectedAuth
        request.getHeader("Accept") shouldEqual HTTPHeaders.ACCEPT_OMG
    }

    @Test
    fun `get_current_user with the correct path`() {
        userFile.mockEnqueueWithHttpCode(mockWebServer)

        eWalletClient.eWalletAPI.getCurrentUser().execute()
        val request = mockWebServer.takeRequest()

        request.path shouldEqual "/api/client/${Endpoints.GET_CURRENT_USER}"
    }

    @Test
    fun `get_setting with the correct path`() {
        getSettingFile.mockEnqueueWithHttpCode(mockWebServer)

        eWalletClient.eWalletAPI.getSettings().execute()
        val request = mockWebServer.takeRequest()
        request.path shouldEqual "/api/client/${Endpoints.GET_SETTINGS}"
    }

    @Test
    fun `list_wallets with the correct path`() {
        getWalletsFile.mockEnqueueWithHttpCode(mockWebServer)

        eWalletClient.eWalletAPI.getWallets().execute()
        val request = mockWebServer.takeRequest()
        request.path shouldEqual "/api/client/${Endpoints.GET_WALLETS}"
    }

    @Test
    fun `get_transactions with the correct path`() {
        getTransactionsFile.mockEnqueueWithHttpCode(mockWebServer)

        val listTransactionParams: TransactionListParams = mock()

        eWalletClient.eWalletAPI.getTransactions(listTransactionParams).execute()
        val request = mockWebServer.takeRequest()
        request.path shouldEqual "/api/client/${Endpoints.GET_TRANSACTIONS}"
    }

    @Test
    fun `logout with the correct path`() {
        userFile.mockEnqueueWithHttpCode(mockWebServer)

        eWalletClient.eWalletAPI.logout().execute()
        val request = mockWebServer.takeRequest()

        request.path shouldEqual "/api/client/${Endpoints.LOGOUT}"
    }

    @Test
    fun `should throws an IllegalStateException if the clientConfiguration is not set`() {
        val error = { EWalletClient.Builder { }.build() }
        error shouldThrow IllegalStateException::class withMessage Exceptions.MSG_NULL_CLIENT_CONFIGURATION
    }

    @Test
    fun `should be able to add the network interceptor for debugging purpose`() {
        val debuggableClient = EWalletClient.Builder {
            debugUrl = mockUrl
            clientConfiguration = config
            callbackExecutor = Executor { it.run() }
            debug = true
            debugOkHttpInterceptors = mockInterceptors
        }.build()

        debuggableClient.client.networkInterceptors().size shouldEqualTo 3
    }

    @Test
    fun `should not add the network interceptor when debug flag is false`() {
        val nonDebuggableClient = EWalletClient.Builder {
            debugUrl = mockUrl
            clientConfiguration = config
            callbackExecutor = Executor { it.run() }
            debug = false
            debugOkHttpInterceptors = mockInterceptors
        }.build()

        nonDebuggableClient.client.networkInterceptors().size shouldEqualTo 0
    }

    private inline fun <reified T> buildResponse(responseText: String): OMGResponse<T> {
        val json = JSONObject(responseText)
        val success = json.getBoolean("success")
        val dataText = json.getJSONObject("data").toString()
        val data = gson.fromJson<T>(dataText, T::class.java)
        return OMGResponse(Versions.EWALLET_API, success, data)
    }
}
