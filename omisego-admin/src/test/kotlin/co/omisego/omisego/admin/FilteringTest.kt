package co.omisego.omisego.admin

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 21/11/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.OMGAPIAdmin
import co.omisego.omisego.admin.extension.mockEnqueueWithHttpCode
import co.omisego.omisego.admin.utils.ResourceFile
import co.omisego.omisego.model.AdminConfiguration
import co.omisego.omisego.model.pagination.Filter
import co.omisego.omisego.model.params.admin.TransactionListParams
import co.omisego.omisego.network.ewallet.EWalletAdmin
import co.omisego.omisego.utils.GsonProvider
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotContain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.concurrent.Executor

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class FilteringTest {
    private lateinit var eWalletAdmin: EWalletAdmin
    private lateinit var mockWebServer: MockWebServer
    private lateinit var mockUrl: HttpUrl
    private lateinit var omgAPIAdmin: OMGAPIAdmin
    private val getTransactionsFile: File by ResourceFile("paginated_transaction.json", "object")

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

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test filtering request type match_any should be sent correctly`() {
        getTransactionsFile.mockEnqueueWithHttpCode(mockWebServer)

        val filters = listOf(
            Filter("id", "eq", "1234"),
            Filter("from.token.id", "eq", "12345")
        )

        omgAPIAdmin.getTransactions(TransactionListParams.create(
            matchAny = filters
        )).execute()

        val body = mockWebServer.takeRequest().body.readUtf8()
        val request = GsonProvider.create().fromJson(body, TransactionListParams::class.java)

        mockWebServer.requestCount shouldBe 1
        body shouldNotContain "match_all" // should not send match_all
        request.matchAny shouldEqual filters
    }

    @Test
    fun `test filtering request type match_all should be sent correctly`() {
        getTransactionsFile.mockEnqueueWithHttpCode(mockWebServer)

        val filters = listOf(
            Filter("id", "eq", "1234"),
            Filter("from.token.id", "eq", "12345")
        )

        omgAPIAdmin.getTransactions(TransactionListParams.create(
            matchAll = filters
        )).execute()

        val body = mockWebServer.takeRequest().body.readUtf8()
        val request = GsonProvider.create().fromJson(body, TransactionListParams::class.java)

        mockWebServer.requestCount shouldBe 1
        body shouldNotContain "match_any" // should not send match_all
        request.matchAll shouldEqual filters
    }

    private fun initMockWebServer() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockUrl = mockWebServer.url("/api/admin/")
    }
}
