package co.omisego.omisego.network

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/8/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.network.ewallet.AuthenticationHeader
import co.omisego.omisego.network.interceptor.AuthenticationTokenInterceptor
import co.omisego.omisego.network.interceptor.HeaderHandler
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.mock
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthenticationTokenInterceptorTest {
    private val mockBody = """
        {
            "data": {
                "object": "authentication_token",
                "user_id": "sample_user_id",
                "authentication_token": "sample_authentication_token"
            }
        }
    """.trimIndent()
    private val mockWebServer by lazy { MockWebServer() }
    private val mockHeaderHandler: HeaderHandler = mock()
    private val mockAuthenticationHeader: AuthenticationHeader = mock()

    @Before
    fun setup() {
        mockWebServer.start()
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should set an authentication token header correctly, when receive an authentication_token object response`() {
        whenever(mockAuthenticationHeader.create("sample_authentication_token", "sample_user_id"))
            .thenReturn("new_authentication_header")

        mockWebServer.enqueue(mockAuthenticationTokenResponse())

        executeWithInterceptor(AuthenticationTokenInterceptor(mockHeaderHandler, mockAuthenticationHeader))

        verify(mockAuthenticationHeader).create("sample_authentication_token", "sample_user_id")
        verify(mockHeaderHandler).setHeader("new_authentication_header")
    }

    private fun executeWithInterceptor(interceptor: AuthenticationTokenInterceptor) {
        val request = Request
            .Builder()
            .url(mockWebServer.url("/"))
            .build()

        OkHttpClient()
            .newBuilder()
            .addInterceptor(interceptor)
            .build()
            .newCall(request)
            .execute()

        mockWebServer.takeRequest()
    }

    private fun mockAuthenticationTokenResponse(): MockResponse {
        val mockResponse = MockResponse()
        mockResponse.setResponseCode(200)
        mockResponse.setBody(mockBody)
        return mockResponse
    }
}
