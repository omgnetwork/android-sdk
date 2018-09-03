package co.omisego.omisego.network

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/8/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.HTTPHeaders
import co.omisego.omisego.constant.enums.AuthScheme
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Test

class HeaderInterceptorTest {
    private val mockWebServer by lazy { MockWebServer() }

    @Before
    fun setup() {
        mockWebServer.start()
        mockWebServer.enqueue(MockResponse())
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should not add header authorization if the authenticationToken is empty`() {
        makeOkHttpCallWithInterceptor(HeaderInterceptor(AuthScheme.ADMIN, ""))
        val request = mockWebServer.takeRequest()
        request.getHeader(HTTPHeaders.AUTHORIZATION) shouldBe null
        request.getHeader(HTTPHeaders.ACCEPT) shouldEqualTo HTTPHeaders.ACCEPT_OMG
    }

    @Test
    fun `should add header authorization if the authenticationToken is not empty`() {
        makeOkHttpCallWithInterceptor(HeaderInterceptor(AuthScheme.ADMIN, "authenticationToken"))
        val request = mockWebServer.takeRequest()
        request.getHeader(HTTPHeaders.AUTHORIZATION) shouldEqualTo "${AuthScheme.ADMIN} authenticationToken"
        request.getHeader(HTTPHeaders.ACCEPT) shouldEqualTo HTTPHeaders.ACCEPT_OMG
    }

    private fun makeOkHttpCallWithInterceptor(headerInterceptor: HeaderInterceptor) {
        OkHttpClient().newBuilder().addInterceptor(headerInterceptor).build()
            .newCall(Request.Builder().url(mockWebServer.url("/")).build()).execute()
    }
}
