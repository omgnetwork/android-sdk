package co.omisego.omisego.network

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 31/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.constant.enums.AuthScheme
import co.omisego.omisego.custom.retrofit2.adapter.OMGCallAdapterFactory
import co.omisego.omisego.custom.retrofit2.converter.OMGConverterFactory
import co.omisego.omisego.custom.retrofit2.executor.MainThreadExecutor
import co.omisego.omisego.model.CredentialConfiguration
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class BaseClientTest {

    class TestBuilder(init: BaseClient.Builder.() -> Unit) : BaseClient.Builder(init)

    class TestCredentialConfiguration(
        override val baseURL: String,
        override val authenticationToken: String,
        override val userId: String?,
        override val apiKey: String?,
        override val authScheme: AuthScheme
    ) : CredentialConfiguration

    private val testCredentialConfiguration: TestCredentialConfiguration by lazy {
        TestCredentialConfiguration(
            "http://test.com/",
            "authenticationToken",
            null,
            "apiKey",
            AuthScheme.Client
        )
    }

    @Test
    fun `build should throw IllegalStateException when credentialConfiguration is null`() {
        val expression = { TestBuilder { clientConfiguration = null }.build() }
        expression shouldThrow IllegalStateException::class withMessage Exceptions.MSG_NULL_CLIENT_CONFIGURATION
    }

    @Test
    fun `build should return BaseClient correctly`() {
        val baseClient = TestBuilder { clientConfiguration = testCredentialConfiguration }.build()
        baseClient.client shouldBeInstanceOf OkHttpClient::class
        baseClient.retrofit shouldBeInstanceOf Retrofit::class
        baseClient.header shouldBeInstanceOf HeaderInterceptor::class
    }

    @Test
    fun `createRetrofit should create a retrofit object correctly`() {
        val retrofit = TestBuilder { clientConfiguration = testCredentialConfiguration }
            .createRetrofit(mock(), mock())

        retrofit.baseUrl() shouldEqual HttpUrl.parse("http://test.com/")
        retrofit.converterFactories().size shouldEqual 2
        retrofit.converterFactories()[1] shouldBeInstanceOf OMGConverterFactory::class
        retrofit.callAdapterFactories().size shouldEqual 2
        retrofit.callAdapterFactories()[0] shouldBeInstanceOf OMGCallAdapterFactory::class
        retrofit.callbackExecutor() shouldBeInstanceOf MainThreadExecutor::class.java
    }
}
