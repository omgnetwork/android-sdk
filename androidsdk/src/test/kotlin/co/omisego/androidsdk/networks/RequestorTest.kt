package co.omisego.androidsdk.networks

import kotlinx.coroutines.experimental.runBlocking
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 11/8/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

@RunWith(MockitoJUnitRunner::class)
class RequestorTest {
    private lateinit var requestor: Requestor
    private lateinit var mockHeaders: Array<Pair<String, String>>
    private lateinit var mockBody: HashMap<String, Any>
    private val MOCK_ENDPOINT = "https://httpbin.org/post"

    @Before
    fun setup() {
        requestor = Requestor
        mockHeaders = arrayOf("Content-Type" to "application/json", "User-Agent" to "mozilla/5.0", "Authorization" to "OMGServer T21pc2VHTyBpcyBhd2Vzb21lIQ==", "Accept" to "application/vnd.omisego.v1+json")
        mockBody = hashMapOf("name" to "OmiseGO", "amount" to 123.33, "done" to false)
    }

    @Test
    fun `network request headers and body should be matched with the requestOptions`() = runBlocking {
        // Arrange
        val requestOptions = RequestOptions().apply {
            setBody(mockBody)
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
}

data class MockHttpBin(val name: String, val amount: Double, val done: Boolean)