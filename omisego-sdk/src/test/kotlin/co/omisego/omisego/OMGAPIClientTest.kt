package co.omisego.omisego

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 6/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.ErrorCode
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.exception.OMGAPIErrorException
import co.omisego.omisego.extension.mockEnqueueWithHttpCode
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.BalanceList
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.Setting
import co.omisego.omisego.model.User
import co.omisego.omisego.model.pagination.Pagination
import co.omisego.omisego.model.pagination.PaginationList
import co.omisego.omisego.network.ewallet.EWalletClient
import co.omisego.omisego.testUtils.GsonProvider
import co.omisego.omisego.utils.OMGEncryptionHelper
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.json.JSONObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import retrofit2.Response
import java.io.File
import java.util.concurrent.Executor

class OMGAPIClientTest {
    @Rule
    @JvmField
    val expectedEx = ExpectedException.none()!!
    private val secretFileName: String = "secret.json" // Replace your secret file here
    private val secret: JSONObject by lazy { loadSecretFile(secretFileName) }
    private val userFile: File by ResourceFile("user.me-post.json")
    private val listBalanceFile: File by ResourceFile("me.list_balances-post.json")
    private val listTransactionsFile: File by ResourceFile("me.list_transactions-post.json")
    private val getSettingFile: File by ResourceFile("me.get_settings-post.json")
    private val errorFile: File by ResourceFile("fail.client-invalid_auth_scheme.json")
    private val gson by lazy { GsonProvider.provide() }
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

        initMockWebServer()

        eWalletClient = EWalletClient.Builder {
            debugUrl = mockUrl
            authenticationToken = auth
            callbackExecutor = Executor { it.run() }
            debug = false
        }.build()

        omgAPIClient = OMGAPIClient(eWalletClient)
    }

    @Test
    fun `OMGAPIClient call list_balance and success callback should be invoked successfully`() {
        val element = gson.fromJson(listBalanceFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        listBalanceFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<BalanceList> = mock()
        omgAPIClient.listBalances().enqueue(callback)

        val expected = gson.fromJson<OMGResponse<BalanceList>>(result.body(), object : TypeToken<OMGResponse<BalanceList>>() {}.type)

        Thread.sleep(100)

        verify(callback, times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient call list_transactions and success callback should be invoked successfully`() {
        val element = gson.fromJson(listTransactionsFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        listTransactionsFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<PaginationList<Transaction>> = mock()
        omgAPIClient.listTransactions(mock()).enqueue(callback)

        val data = result.body()!!.asJsonObject.getAsJsonObject("data").getAsJsonArray("data")
        val transactionList = gson.fromJson<List<Transaction>>(data, object : TypeToken<List<Transaction>>() {}.type)

        val expected = OMGResponse(
                Versions.EWALLET_API,
                true,
                PaginationList(
                        transactionList,
                        Pagination(10, true, true, 1)
                )
        )

        Thread.sleep(100)

        verify(callback, times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient should call get_current_user and success`() {
        val element = gson.fromJson(userFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        userFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<User> = mock()
        omgAPIClient.getCurrentUser().enqueue(callback)

        val expected = gson.fromJson<OMGResponse<User>>(result.body(), object : TypeToken<OMGResponse<User>>() {}.type)

        Thread.sleep(100)

        verify(callback, times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient should call get_setting and success`() {
        val element = gson.fromJson(getSettingFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        getSettingFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<Setting> = mock()
        omgAPIClient.getSettings().enqueue(callback)

        val expected = gson.fromJson<OMGResponse<Setting>>(result.body(), object : TypeToken<OMGResponse<Setting>>() {}.type)

        Thread.sleep(100)

        verify(callback, times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient should resolve APIError from the EWalletAPI gracefully`() {
        val element = gson.fromJson(errorFile.readText(), JsonElement::class.java)
        errorFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<User> = mock()
        omgAPIClient.getCurrentUser().enqueue(callback)

        val data = element.asJsonObject.get("data")
        val apiError = APIError(ErrorCode.from(data.asJsonObject.get("code").asString), data.asJsonObject.get("description").asString)
        val expected = OMGResponse(Versions.EWALLET_API, false, apiError)

        Thread.sleep(100)
        verify(callback, times(1)).fail(expected)
    }

    @Test
    fun `OMGAPIClient should be executed when API return success true correctly`() {
        val expected = gson.fromJson<OMGResponse<User>>(userFile.readText(),
            object : TypeToken<OMGResponse<User>>() {}.type)

        userFile.mockEnqueueWithHttpCode(mockWebServer)

        val response = omgAPIClient.getCurrentUser().execute()
        response.body() shouldEqual expected
    }

    @Test
    fun `OMGAPIClient should be executed when API return success false correctly`() {
        expectedEx.expect(OMGAPIErrorException::class.java)
        val apiError = APIError(ErrorCode.CLIENT_INVALID_AUTH_SCHEME,
            "The provided authentication scheme is not supported")
        expectedEx.expectMessage(OMGResponse(Versions.EWALLET_API, false, apiError).toString())

        errorFile.mockEnqueueWithHttpCode(mockWebServer)

        omgAPIClient.getCurrentUser().execute()
    }

    @Test
    fun `OMGAPIClient should be cloned and can be called to get_current_user successfully`() {
        val element = gson.fromJson(userFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        userFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<User> = mock()
        omgAPIClient.getCurrentUser().clone().enqueue(callback)

        val expected = gson.fromJson<OMGResponse<User>>(result.body(), object : TypeToken<OMGResponse<User>>() {}.type)

        Thread.sleep(100)

        verify(callback, times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient should delegate an error caused by API 500 to APIError successfully`() {
        errorFile.mockEnqueueWithHttpCode(mockWebServer, 500)

        val callback: OMGCallback<User> = mock()
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

    private fun initMockWebServer() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockUrl = mockWebServer.url("/api/")
    }
}
