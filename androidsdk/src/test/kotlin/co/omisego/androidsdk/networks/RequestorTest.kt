package co.omisego.androidsdk.networks

import co.omisego.androidsdk.exceptions.OmiseGOServerErrorException
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.experimental.runBlocking
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 11/8/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

@RunWith(MockitoJUnitRunner::class)
class RequestorTest {
    private lateinit var httpConnection: HttpConnection
    private lateinit var requestor: Requestor
    private lateinit var mockHeaders: Array<Pair<String, String>>
    private lateinit var mockBody: HashMap<String, Any>
    private val MOCK_BASE_URL = "https://httpbin.org/"
    private val MOCK_ENDPOINT = "post"

    @Mock
    private lateinit var mockHttpConnection: HttpConnection
    private lateinit var mockRequestor: Requestor

    @Before
    fun setup() {
        httpConnection = DefaultHttpConnection(MOCK_BASE_URL)
        requestor = Requestor(httpConnection)
        mockRequestor = Requestor(mockHttpConnection)
        mockHeaders = arrayOf("Content-Type" to "application/json", "User-Agent" to "mozilla/5.0", "Authorization" to "OMGServer T21pc2VHTyBpcyBhd2Vzb21lIQ==", "Accept" to "application/vnd.omisego.v1+json")
        mockBody = hashMapOf("name" to "OmiseGO", "amount" to 123.33, "done" to false)
    }

    @Test
    fun `network request headers and body should be matched with the requestOptions`() = runBlocking {
        // Arrange
        val requestOptions = RequestOptions().apply {
            setBody(*mockBody.toList().toTypedArray())
            setHeaders(*mockHeaders)
        }
        val expectedBody = MockHttpBin(mockBody["name"] as String, mockBody["amount"] as Double, mockBody["done"] as Boolean)

        // Action
        val job = requestor.asyncRequest(MOCK_ENDPOINT, requestOptions)

        val resp = job.await() // Asynchronous thread is already finished its job.

        resp shouldNotBe null
        val bodyJson = JSONObject(resp.response).getJSONObject("json")
        val headerJson = JSONObject(resp.response).getJSONObject("headers")
        val actualBody = MockHttpBin(bodyJson.getString("name"), bodyJson.getDouble("amount"), bodyJson.getBoolean("done"))

        // Assert thread
        Thread.currentThread().name shouldEqual "main" // Validate that we're already in the main thread now.

        // Assert body
        actualBody shouldEqual expectedBody

        // Assert headers
        headerJson.getString("Content-Type") shouldEqual mockHeaders[0].second
        headerJson.getString("User-Agent") shouldEqual mockHeaders[1].second
        headerJson.getString("Authorization") shouldEqual mockHeaders[2].second
        headerJson.getString("Accept") shouldEqual mockHeaders[3].second
    }

    @Test
    fun `network response should not success when parsing response throw IOException`() = runBlocking {
        whenever(mockHttpConnection.response()).thenThrow(IOException::class.java)

        val job = mockRequestor.asyncRequest(MOCK_ENDPOINT, RequestOptions().apply {
            setHeaders(*mockHeaders)
            setBody(*mockBody.toList().toTypedArray())
        })

        val resp = job.await()

        resp.response shouldBe null
        resp.success shouldBe false

        // Verify that input stream should be closed.
        verify(mockHttpConnection, times(1)).closeInputStream()
    }

    @Test
    fun `network response should not success when parsing response throw Exception`() = runBlocking {
        whenever(mockHttpConnection.response()).thenThrow(Exception::class.java)

        val job = mockRequestor.asyncRequest(MOCK_ENDPOINT, RequestOptions().apply {
            setHeaders(*mockHeaders)
            setBody(*mockBody.toList().toTypedArray())
        })

        val resp = job.await()

        resp.response shouldBe null
        resp.success shouldBe false

        // Verify that input stream should be closed.
        verify(mockHttpConnection, times(1)).closeInputStream()
    }

    @Test
    fun `network response should not success when parsing response throw OmiseGOServerErrorException`() = runBlocking {
        whenever(mockHttpConnection.response()).thenThrow(OmiseGOServerErrorException::class.java)

        val job = mockRequestor.asyncRequest(MOCK_ENDPOINT, RequestOptions().apply {
            setHeaders(*mockHeaders)
            setBody(*mockBody.toList().toTypedArray())
        })

        val resp = job.await()

        resp.response shouldBe "OmiseGO server error with code 500"
        resp.success shouldBe false

        // Verify that input stream should be closed.
        verify(mockHttpConnection, times(1)).closeInputStream()
    }
    data class MockHttpBin(val name: String, val amount: Double, val done: Boolean)
}

