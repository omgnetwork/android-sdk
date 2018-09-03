package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 31/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.AuthScheme
import co.omisego.omisego.model.CredentialConfiguration
import co.omisego.omisego.network.HeaderInterceptor
import okhttp3.Interceptor
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class OkHttpHelperTest {

    data class TestCredentialConfiguration(
        override val baseURL: String,
        override val authenticationToken: String,
        override val userId: String?,
        override val apiKey: String?,
        override val authScheme: AuthScheme
    ) : CredentialConfiguration

    private val clientConfiguration: TestCredentialConfiguration by lazy {
        TestCredentialConfiguration(
            "http://test.com/",
            "authenticationToken",
            null,
            "apiKey",
            AuthScheme.Client
        )
    }

    private val okHttpHelper by lazy { OkHttpHelper() }

    @Test
    fun `createHeader should return HeaderInterceptor correctly`() {
        val headerInterceptor = okHttpHelper.createHeader(clientConfiguration)
        headerInterceptor shouldBeInstanceOf HeaderInterceptor::class.java
        headerInterceptor.authScheme shouldEqual AuthScheme.Client
        headerInterceptor.authenticationToken shouldEqualTo "YXBpS2V5OmF1dGhlbnRpY2F0aW9uVG9rZW4="
    }

    @Test
    fun `createClient should return OKHttpClient when debug is true correctly`() {
        val mockHeaderInterceptor: HeaderInterceptor = mock()
        val mockNetworkInterceptor: Interceptor = mock()
        with(okHttpHelper.createClient(true, listOf(mockHeaderInterceptor), mutableListOf(mockNetworkInterceptor))) {
            interceptors().size shouldEqualTo 1
            interceptors()[0] shouldBeInstanceOf HeaderInterceptor::class.java
            networkInterceptors().size shouldEqualTo 1
            networkInterceptors()[0] shouldBe mockNetworkInterceptor
        }
    }

    @Test
    fun `createClient should return OKHttpClient when debug is false correctly`() {
        val mockHeaderInterceptor: HeaderInterceptor = mock()
        val mockNetworkInterceptor: Interceptor = mock()
        with(okHttpHelper.createClient(false, listOf(mockHeaderInterceptor), mutableListOf(mockNetworkInterceptor))) {
            interceptors().size shouldEqualTo 1
            interceptors()[0] shouldBeInstanceOf HeaderInterceptor::class.java
            networkInterceptors().size shouldEqualTo 0
        }
    }
}
