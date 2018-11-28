package co.omisego.omisego.admin

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 21/11/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.OMGAPIAdmin
import co.omisego.omisego.admin.extension.mockEnqueueWithHttpCode
import co.omisego.omisego.admin.utils.GsonDelegator
import co.omisego.omisego.admin.utils.ResourceFile
import co.omisego.omisego.model.AdminConfiguration
import co.omisego.omisego.model.filterable.Filterable
import co.omisego.omisego.model.filterable.buildFilterList
import co.omisego.omisego.model.pagination.Paginable
import co.omisego.omisego.model.params.admin.TransactionListParams
import co.omisego.omisego.network.ewallet.EWalletAdmin
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqualTo
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
class FilteringTest : GsonDelegator() {
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
    fun `test a filter list builder should create a filter list correctly`() {
        val filterList = buildFilterList<Filterable.TransactionFields> { field ->
            /* Test number comparator */
            add(field.fromAmount eq 100)
            add(field.fromAmount neq 101)
            add(field.toAmount gt 1.3)
            add(field.toAmount gte 2.8)
            add(field.toAmount lt 10)
            add(field.toAmount lte 1.0)

            /* Test string comparator */
            add(field.status eq Paginable.Transaction.TransactionStatus.CONFIRMED)
            add(field.type eq "some_type")
            add(field.id startsWith "123")
            add(field.status contains "confirm")

            /* Test null comparator */
            add("unknown" eq null)
            add("unknown" neq null)

            /* Test boolean comparator */
            add("success" eq true)
            add("fail" neq true)
        }

        val expectedJson = """
            [
              {
                "field": "from_amount",
                "comparator": "eq",
                "value": 100
              },
              {
                "field": "from_amount",
                "comparator": "neq",
                "value": 101
              },
              {
                "field": "to_amount",
                "comparator": "gt",
                "value": 1.3
              },
              {
                "field": "to_amount",
                "comparator": "gte",
                "value": 2.8
              },
              {
                "field": "to_amount",
                "comparator": "lt",
                "value": 10
              },
              {
                "field": "to_amount",
                "comparator": "lte",
                "value": 1.0
              },
              {
                "field": "status",
                "comparator": "eq",
                "value": "confirmed"
              },
              {
                "field": "type",
                "comparator": "eq",
                "value": "some_type"
              },
              {
                "field": "id",
                "comparator": "starts_with",
                "value": "123"
              },
              {
                "field": "status",
                "comparator": "contains",
                "value": "confirm"
              },
              {
                "field": "unknown",
                "comparator": "eq",
                "value": null
              },
              {
                "field": "unknown",
                "comparator": "neq",
                "value": null
              },
              {
                "field": "success",
                "comparator": "eq",
                "value": true
              },
              {
                "field": "fail",
                "comparator": "neq",
                "value": true
              }
            ]
        """.trimIndent()

        gson.toJson(filterList) shouldEqualTo expectedJson
    }
    @Test
    fun `test filtering request should be sent correctly`() {
        getTransactionsFile.mockEnqueueWithHttpCode(mockWebServer)

        val filters = buildFilterList {
            add("id" eq "1234")
            add("from.token.id" eq "12345")
        }

        val transactionListParams = TransactionListParams.create(
            matchAny = filters
        )

        omgAPIAdmin.getTransactions(transactionListParams).execute()

        val params = mockWebServer.takeRequest().body.readUtf8()

        val expectedRequestParams = """
            {
              "page": 1,
              "per_page": 10,
              "sort_by": "created_at",
              "sort_dir": "desc",
              "match_any": [
                {
                  "field": "id",
                  "comparator": "eq",
                  "value": "1234"
                },
                {
                  "field": "from.token.id",
                  "comparator": "eq",
                  "value": "12345"
                }
              ]
            }
        """.trimIndent()

        mockWebServer.requestCount shouldBe 1
        params shouldEqualTo expectedRequestParams
    }

    private fun initMockWebServer() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockUrl = mockWebServer.url("/api/admin/")
    }
}
