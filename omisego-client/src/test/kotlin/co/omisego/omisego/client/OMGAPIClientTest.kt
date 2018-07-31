package co.omisego.omisego.client

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 6/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.client.constant.Versions
import co.omisego.omisego.client.model.ClientConfiguration
import co.omisego.omisego.client.network.ewallet.EWalletClient
import co.omisego.omisego.client.util.GsonDelegator
import co.omisego.omisego.client.util.ResourceFile
import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.exception.OMGAPIErrorException
import co.omisego.omisego.client.extension.mockEnqueueWithHttpCode
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.Setting
import co.omisego.omisego.model.User
import co.omisego.omisego.model.WalletList
import co.omisego.omisego.model.pagination.Pagination
import co.omisego.omisego.model.pagination.PaginationList
import co.omisego.omisego.model.transaction.Transaction
import co.omisego.omisego.model.transaction.consumption.TransactionConsumption
import co.omisego.omisego.model.transaction.request.TransactionRequest
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockito_kotlin.timeout
import com.nhaarman.mockito_kotlin.verify
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response
import java.io.File
import java.util.concurrent.Executor

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class OMGAPIClientTest : GsonDelegator() {
    private val getWalletsFile: File by ResourceFile("get_wallets.json")
    private val transactionFile: File by ResourceFile("transaction.json")
    private val userFile: File by ResourceFile("user.json")
    private val getTransactionsFile: File by ResourceFile("get_transactions.json")
    private val transactionRequestFile: File by ResourceFile("transaction_request.json")
    private val consumeTransactionRequestFile: File by ResourceFile("transaction_consumption.json")
    private val getSettingFile: File by ResourceFile("setting.json")
    private val errorFile: File by ResourceFile("error-invalid_auth.json")
    private val connectionTimeout = 1_000L // ms
    private lateinit var eWalletClient: EWalletClient
    private lateinit var mockWebServer: MockWebServer
    private lateinit var mockUrl: HttpUrl
    private lateinit var omgAPIClient: OMGAPIClient

    @Before
    fun setup() {
        initMockWebServer()
        val config = ClientConfiguration(
            "base_url",
            "api_key",
            "auth_token"
        )

        eWalletClient = EWalletClient.Builder {
            debugUrl = mockUrl
            callbackExecutor = Executor { it.run() }
            clientConfiguration = config
        }.build()

        omgAPIClient = OMGAPIClient(eWalletClient)
    }

    @Test
    fun `OMGAPIClient call list_wallets and success callback should be invoked successfully`() {
        val element = gson.fromJson(getWalletsFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        getWalletsFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<WalletList> = mock()
        omgAPIClient.getWallets().enqueue(callback)

        val expected = gson.fromJson<OMGResponse<WalletList>>(result.body(), object : TypeToken<OMGResponse<WalletList>>() {}.type)

        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient call get_transactions and success callback should be invoked successfully`() {
        val element = gson.fromJson(getTransactionsFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        getTransactionsFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<PaginationList<Transaction>> = mock()
        omgAPIClient.getTransactions(mock()).enqueue(callback)

        val data = result.body()!!.asJsonObject.getAsJsonObject("data").getAsJsonArray("data")
        val transactionList = gson.fromJson<List<Transaction>>(data, object : TypeToken<List<Transaction>>() {}.type)

        val expected = OMGResponse<PaginationList<Transaction>>(
            Versions.EWALLET_API,
            true,
            PaginationList(
                transactionList,
                Pagination(10, true, true, 1)
            )
        )

        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient should call create_transaction_request successfully`() {
        val element = gson.fromJson(transactionRequestFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        transactionRequestFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<TransactionRequest> = mock()

        omgAPIClient.createTransactionRequest(mock()).enqueue(callback)

        val data = result.body()!!.asJsonObject.getAsJsonObject("data")
        val transactionRequest = gson.fromJson<TransactionRequest>(data, object : TypeToken<TransactionRequest>() {}.type)

        val expected = OMGResponse(
            Versions.EWALLET_API,
            true,
            transactionRequest
        )

        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient should call consume_transaction successfully`() {
        val element = gson.fromJson(consumeTransactionRequestFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        consumeTransactionRequestFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<TransactionConsumption> = mock()

        omgAPIClient.consumeTransactionRequest(mock()).enqueue(callback)

        val data = result.body()!!.asJsonObject.getAsJsonObject("data")
        val transactionConsumption = gson.fromJson<TransactionConsumption>(data, object : TypeToken<TransactionConsumption>() {}.type)

        val expected = OMGResponse(
            Versions.EWALLET_API,
            true,
            transactionConsumption
        )

        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient should call create_transaction and parse successfully`() {
        val element = gson.fromJson(transactionFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        transactionFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<Transaction> = mock()

        omgAPIClient.createTransaction(mock()).enqueue(callback)

        val data = result.body()!!.asJsonObject.getAsJsonObject("data")
        val transaction = gson.fromJson<Transaction>(data, object : TypeToken<Transaction>() {}.type)

        omgAPIClient.createTransaction(mock()).enqueue(callback)

        val expected = OMGResponse(
            Versions.EWALLET_API,
            true,
            transaction
        )

        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient should call get_transaction_request successfully`() {
        val element = gson.fromJson(transactionRequestFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        transactionRequestFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<TransactionRequest> = mock()

        omgAPIClient.retrieveTransactionRequest(mock()).enqueue(callback)

        val data = result.body()!!.asJsonObject.getAsJsonObject("data")
        val transactionRequest = gson.fromJson<TransactionRequest>(data, object : TypeToken<TransactionRequest>() {}.type)

        val expected = OMGResponse(
            Versions.EWALLET_API,
            true,
            transactionRequest
        )

        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient should call get_current_user and success`() {
        val element = gson.fromJson(userFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        userFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<User> = mock()
        omgAPIClient.getCurrentUser().enqueue(callback)

        val expected = gson.fromJson<OMGResponse<User>>(result.body(), object : TypeToken<OMGResponse<User>>() {}.type)

        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient should call get_setting and success`() {
        val element = gson.fromJson(getSettingFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        getSettingFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<Setting> = mock()
        omgAPIClient.getSettings().enqueue(callback)

        val expected = gson.fromJson<OMGResponse<Setting>>(result.body(), object : TypeToken<OMGResponse<Setting>>() {}.type)

        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
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

        verify(callback, timeout(connectionTimeout).times(1)).fail(expected)
    }

    @Test
    fun `OMGAPIClient should be executed when API return success true correctly`() {
        val expected = gson.fromJson<OMGResponse<User>>(
            userFile.readText(),
            object : TypeToken<OMGResponse<User>>() {}.type
        )
        userFile.mockEnqueueWithHttpCode(mockWebServer)

        val response = omgAPIClient.getCurrentUser().execute()
        response.body() shouldEqual expected
    }

    @Test
    fun `OMGAPIClient should be executed when API return success false correctly`() {
        val apiError = APIError(
            ErrorCode.CLIENT_INVALID_AUTH_SCHEME,
            "The provided authentication scheme is not supported"
        )

        errorFile.mockEnqueueWithHttpCode(mockWebServer)

        val errorFun = { omgAPIClient.getCurrentUser().execute() }
        errorFun shouldThrow OMGAPIErrorException::class withMessage
            OMGResponse(Versions.EWALLET_API, false, apiError).toString()
    }

    @Test
    fun `OMGAPIClient should be cloned and can be called to get_current_user successfully`() {
        val element = gson.fromJson(userFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        userFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<User> = mock()
        omgAPIClient.getCurrentUser().clone().enqueue(callback)

        val expected = gson.fromJson<OMGResponse<User>>(result.body(), object : TypeToken<OMGResponse<User>>() {}.type)

        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIClient should delegate an error caused by API 500 to APIError successfully`() {
        errorFile.mockEnqueueWithHttpCode(mockWebServer, 500)

        val callback: OMGCallback<User> = mock()
        omgAPIClient.getCurrentUser().enqueue(callback)

        val apiError = APIError(ErrorCode.SERVER_INTERNAL_SERVER_ERROR, "The EWallet API was 500 Internal Server Error")
        val expected = OMGResponse(Versions.EWALLET_API, false, apiError)

        verify(callback, timeout(connectionTimeout).times(1)).fail(expected)
    }

    private fun initMockWebServer() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockUrl = mockWebServer.url("/api/client/")
    }
}
