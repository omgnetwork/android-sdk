package co.omisego.omisego.admin

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.OMGAPIAdmin
import co.omisego.omisego.admin.extension.mockEnqueueWithHttpCode
import co.omisego.omisego.admin.utils.GsonDelegator
import co.omisego.omisego.admin.utils.ResourceFile
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.model.Account
import co.omisego.omisego.model.AdminConfiguration
import co.omisego.omisego.model.AdminAuthenticationToken
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.Token
import co.omisego.omisego.model.Wallet
import co.omisego.omisego.model.pagination.Pagination
import co.omisego.omisego.model.pagination.PaginationList
import co.omisego.omisego.model.params.LoginParams
import co.omisego.omisego.model.params.WalletParams
import co.omisego.omisego.model.transaction.Transaction
import co.omisego.omisego.network.ewallet.EWalletAdmin
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockito_kotlin.timeout
import com.nhaarman.mockito_kotlin.verify
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.mock
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
class OMGAPIAdminTest : GsonDelegator() {
    private lateinit var eWalletAdmin: EWalletAdmin
    private lateinit var mockWebServer: MockWebServer
    private lateinit var mockUrl: HttpUrl
    private lateinit var omgAPIAdmin: OMGAPIAdmin

    private val connectionTimeout = 1_000L // ms
    private val authenticationTokenFile: File by ResourceFile("authentication_token.json", "object")
    private val getTransactionsFile: File by ResourceFile("paginated_transaction.json", "object")
    private val getAccountsFile: File by ResourceFile("paginated_account.json", "object")
    private val getTokensFile: File by ResourceFile("paginated_token.json", "object")
    private val getWalletsFile: File by ResourceFile("paginated_wallet.json", "object")
    private val getWalletFile: File by ResourceFile("wallet.json", "object")
    private val transactionFile: File by ResourceFile("transaction.json", "object")

    @Before
    fun setup() {
        initMockWebServer()
        val config = AdminConfiguration(
            "http://base_url"
        )

        eWalletAdmin = EWalletAdmin.Builder {
            callbackExecutor = Executor { it.run() }
            clientConfiguration = config
            debugUrl = mockUrl
        }.build()

        omgAPIAdmin = OMGAPIAdmin(eWalletAdmin)
    }

    private fun initMockWebServer() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockUrl = mockWebServer.url("/api/admin/")
    }

    @Test
    fun `OMGAPIAdmin call login and success callback should be invoked successfully`() {
        val element = gson.fromJson(authenticationTokenFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        authenticationTokenFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<AdminAuthenticationToken> = mock()
        omgAPIAdmin.login(LoginParams("email", "password")).enqueue(callback)

        val expected = gson.fromJson<OMGResponse<AdminAuthenticationToken>>(result.body(), object : TypeToken<OMGResponse<AdminAuthenticationToken>>() {}.type)
        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIAdmin call get_transactions and success callback should be invoked successfully`() {
        val element = gson.fromJson(getTransactionsFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        getTransactionsFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<PaginationList<Transaction>> = mock()
        omgAPIAdmin.getTransactions(mock()).enqueue(callback)

        val data = result.body()!!.asJsonObject.getAsJsonObject("data").getAsJsonArray("data")
        val transactionList = gson.fromJson<List<Transaction>>(data, object : TypeToken<List<Transaction>>() {}.type)

        val expected = OMGResponse(
            Versions.EWALLET_API,
            true,
            PaginationList(
                transactionList,
                Pagination(2, false, true, 1)
            )
        )

        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIAdmin call get_accounts and success callback should be invoked successfully`() {
        val element = gson.fromJson(getAccountsFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        getAccountsFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<PaginationList<Account>> = mock()
        omgAPIAdmin.getAccounts(mock()).enqueue(callback)

        val data = result.body()!!.asJsonObject.getAsJsonObject("data").getAsJsonArray("data")
        val accountList = gson.fromJson<List<Account>>(data, object : TypeToken<List<Account>>() {}.type)

        val expected = OMGResponse(
            Versions.EWALLET_API,
            true,
            PaginationList(
                accountList,
                Pagination(10, true, true, 1)
            )
        )

        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIAdmin call get_tokens and success callback should be invoked successfully`() {
        val element = gson.fromJson(getTokensFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        getTokensFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<PaginationList<Token>> = mock()
        omgAPIAdmin.getTokens(mock()).enqueue(callback)

        val data = result.body()!!.asJsonObject.getAsJsonObject("data").getAsJsonArray("data")
        val tokenList = gson.fromJson<List<Token>>(data, object : TypeToken<List<Token>>() {}.type)

        val expected = OMGResponse(
            Versions.EWALLET_API,
            true,
            PaginationList(
                tokenList,
                Pagination(30, true, true, 1)
            )
        )

        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIAdmin call get_account_wallets and success callback should be invoked successfully`() {
        val element = gson.fromJson(getWalletsFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        getWalletsFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<PaginationList<Wallet>> = mock()
        omgAPIAdmin.getAccountWallets(mock()).enqueue(callback)

        val data = result.body()!!.asJsonObject.getAsJsonObject("data").getAsJsonArray("data")
        val accountWalletList = gson.fromJson<List<Wallet>>(data, object : TypeToken<List<Wallet>>() {}.type)

        val expected = OMGResponse(
            Versions.EWALLET_API,
            true,
            PaginationList(
                accountWalletList,
                Pagination(10, true, true, 1)
            )
        )

        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIAdmin call get_user_wallets and success callback should be invoked successfully`() {
        val element = gson.fromJson(getWalletsFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        getWalletsFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<PaginationList<Wallet>> = mock()
        omgAPIAdmin.getUserWallets(mock()).enqueue(callback)

        val data = result.body()!!.asJsonObject.getAsJsonObject("data").getAsJsonArray("data")
        val userWalletList = gson.fromJson<List<Wallet>>(data, object : TypeToken<List<Wallet>>() {}.type)

        val expected = OMGResponse(
            Versions.EWALLET_API,
            true,
            PaginationList(
                userWalletList,
                Pagination(10, true, true, 1)
            )
        )

        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIAdmin call get_wallet and success callback should be invoked successfully`() {
        val element = gson.fromJson(getWalletFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        getWalletFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<Wallet> = mock()
        omgAPIAdmin.getWallet(WalletParams("test-address")).enqueue(callback)

        val expected = gson.fromJson<OMGResponse<Wallet>>(result.body(), object : TypeToken<OMGResponse<Wallet>>() {}.type)
        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }

    @Test
    fun `OMGAPIAdmin call transfer and success callback should be invoked successfully`() {
        val element = gson.fromJson(transactionFile.readText(), JsonElement::class.java)
        val result = Response.success(element)
        transactionFile.mockEnqueueWithHttpCode(mockWebServer)

        val callback: OMGCallback<Transaction> = mock()
        omgAPIAdmin.transfer(mock()).enqueue(callback)

        val expected = gson.fromJson<OMGResponse<Transaction>>(result.body(), object : TypeToken<OMGResponse<Transaction>>() {}.type)
        verify(callback, timeout(connectionTimeout).times(1)).success(expected)
    }
}