package co.omisego.androidsdk

import co.omisego.androidsdk.models.*
import co.omisego.androidsdk.networks.RequestOptions
import co.omisego.androidsdk.networks.Requestor
import co.omisego.androidsdk.utils.APIErrorCode
import co.omisego.androidsdk.utils.ParseStrategy
import co.omisego.androidsdk.utils.Serializer
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.amshove.kluent.shouldNotBeInstanceOf
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.test.assertTrue

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 11/10/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */


class OMGApiClientTest {
    private val TEST_AUTHORIZATION_TOKEN = "OMGClient MTQ4MnFOeFBleTdBNF9ycktrQU9iNGtBT1RzRDJIb0x5c1M3ZVExWmQzWTo5WFdoSWR0a0FOTUZ4RGhBRlRVUUJTaFItakdvR2V5b0MyRjQ0ZFpmcGlJ"
    private var secret: File? = null
    @Before
    fun setUp() {
        val resourceUserURL = javaClass.classLoader.getResource("secret.json") // This is invisible because it's stored in local ("secret").
        secret = File(resourceUserURL.path)

        OMGApiClient.init(TEST_AUTHORIZATION_TOKEN, EmptyCoroutineContext)
    }

    private fun asyncLogin(): Deferred<String> {
        return async(EmptyCoroutineContext) {
            val authenticationServer = JSONObject(secret!!.readText()).getString("authenticationTokenServer")
            val job = Requestor.asyncRequest("https://kubera.omisego.io/login", RequestOptions().apply {
                setHeaders("Authorization" to "OMGServer $authenticationServer",
                        "Accept" to "application/vnd.omisego.v1+json",
                        "Content-Type" to "application/vnd.omisego.v1+json")

                setBody(hashMapOf("provider_user_id" to "user12345678"))
            })

            val response = job.await().response
            val general = Serializer(ParseStrategy.GENERAL).serialize(response!!)

            return@async general.data.getJSONObject("data").getString("authentication_token")
        }
    }

    @Test
    fun `get user success`() = runBlocking {
        // Arrange
        var actualResponse: Response<Any>? = null

        // Just don't care about thread here. Because in android, will work properly.
        // Action
        OMGApiClient.getCurrentUser(object : Callback<User> {
            override fun success(response: Response<User>) {
                actualResponse = response
            }

            override fun fail(response: Response<ApiError>) {
                actualResponse = response
            }
        })

        delay(3000)

        // Assert
        actualResponse shouldNotBe null
        val model = actualResponse!!.data
        model shouldNotBeInstanceOf ApiError::class

        val user = model as User
        user.id shouldNotBe null
        user.metaData shouldNotBe null
        user.providerUserId shouldNotBe null
        user.username shouldNotBe null
    }

    @Test
    fun `get user failed because invalid auth scheme`() = runBlocking {
        // Arrange
        var actualResponse: Response<Any>? = null
        OMGApiClient.init("wrong token", EmptyCoroutineContext)

        // Just don't care about thread here. Because in android, will work properly.
        // Action
        OMGApiClient.getCurrentUser(object : Callback<User> {
            override fun success(response: Response<User>) {
                actualResponse = response
            }

            override fun fail(response: Response<ApiError>) {
                actualResponse = response
            }
        })

        delay(3000)

        // Assert
        actualResponse shouldNotBe null
        val model = actualResponse!!.data
        model shouldNotBeInstanceOf User::class

        val user = model as ApiError
        user.code shouldEqual APIErrorCode.CLIENT_INVALID_AUTH_SCHEME
    }

    @Test
    fun `logout success`() = runBlocking {
        // Arrange
        secret shouldNotBe null
        val secretObject = JSONObject(secret!!.readText())

        var actualResponse: Response<Any>? = null
        val token = asyncLogin().await()

        val apiKey = secretObject.getString("apiKey")

        val authenticationToken = "OMGClient ${String(Base64.getEncoder().encode("$apiKey:$token".toByteArray()))}"

        // Action
        OMGApiClient.init(authenticationToken, EmptyCoroutineContext)

        OMGApiClient.logout(object : Callback<String> {
            override fun success(response: Response<String>) {
                println(response.toString())
                actualResponse = response
            }

            override fun fail(response: Response<ApiError>) {
                println(response.toString())
                actualResponse = response
            }
        })

        delay(3000)

        // Assert
        actualResponse shouldNotBe null
        actualResponse?.success shouldEqual true

        // Try to use the same token
        OMGApiClient.init(authenticationToken, EmptyCoroutineContext)
        OMGApiClient.logout(object : Callback<String> {
            override fun success(response: Response<String>) {
                println(response.toString())
                actualResponse = response
            }

            override fun fail(response: Response<ApiError>) {
                println(response.toString())
                actualResponse = response
            }
        })

        delay(3000)

        // Assert that it should fail because token has been invalidated
        actualResponse shouldNotBe null
        actualResponse?.success shouldEqual false
        actualResponse?.data shouldBeInstanceOf ApiError::class
        val error = actualResponse?.data as ApiError
        error.code shouldEqual "user:access_token_expired"
    }

    @Test
    fun `list balances success`() = runBlocking {
        // Arrange
        var actualResponse: Response<Any>? = null

        // Just don't care about thread here. Because in android, will work properly.
        // Action
        OMGApiClient.listBalances(object : Callback<List<Address>> {
            override fun success(response: Response<List<Address>>) {
                actualResponse = response
            }

            override fun fail(response: Response<ApiError>) {
                actualResponse = response
            }
        })

        delay(3000)

        // Assert
        actualResponse shouldNotBe null

        val model = actualResponse!!.data
        assertTrue(model is List<*>)

        val listAddress = model as List<Address>
        listAddress.size shouldEqual 1
        listAddress[0].balances.size shouldEqual 2
        listAddress[0].balances[0].amount shouldEqual 10000.0
        listAddress[0].balances[0].mintedToken.symbol shouldEqual "OMG"
        listAddress[0].balances[0].mintedToken.name shouldEqual "OmiseGO"
        listAddress[0].balances[0].mintedToken.subUnitToUnit shouldEqual 100.0
    }

    @Test
    fun `list balances should fail because wrong token given`() = runBlocking {
        // Arrange
        var actualResponse: Response<Any>? = null
        OMGApiClient.init("wrong token", EmptyCoroutineContext)

        // Just don't care about thread here. Because in android, will work properly.
        // Action
        OMGApiClient.listBalances(object : Callback<List<Address>> {
            override fun success(response: Response<List<Address>>) {
                actualResponse = response
            }

            override fun fail(response: Response<ApiError>) {
                actualResponse = response
            }
        })

        delay(3000)

        // Assert
        actualResponse shouldNotBe null

        println(actualResponse)

        val model = actualResponse!!.data
        assertTrue(model !is List<*>)

        val setting = model as ApiError
        setting.code shouldEqual APIErrorCode.CLIENT_INVALID_AUTH_SCHEME
    }

    @Test
    fun `get settings success`() = runBlocking {
        // Arrange
        var actualResponse: Response<Any>? = null

        // Just don't care about thread here. Because in android, will work properly.
        // Action
        OMGApiClient.getSettings(object : Callback<Setting> {
            override fun success(response: Response<Setting>) {
                actualResponse = response
            }

            override fun fail(response: Response<ApiError>) {
                actualResponse = response
            }
        })

        delay(3000)

        // Assert
        actualResponse shouldNotBe null
        val model = actualResponse!!.data
        model shouldNotBeInstanceOf ApiError::class

        val setting = model as Setting
        setting.mintedTokens.size shouldEqual 2
    }

    @Test
    fun `get settings failed because invalid auth scheme`() = runBlocking {
        // Arrange
        var actualResponse: Response<Any>? = null
        OMGApiClient.init("wrong token", EmptyCoroutineContext)

        // Just don't care about thread here. Because in android, will work properly.
        // Action
        OMGApiClient.getSettings(object : Callback<Setting> {
            override fun success(response: Response<Setting>) {
                actualResponse = response
            }

            override fun fail(response: Response<ApiError>) {
                actualResponse = response
            }
        })

        delay(3000)

        // Assert
        actualResponse shouldNotBe null
        val model = actualResponse!!.data
        model shouldNotBeInstanceOf Setting::class

        val setting = model as ApiError
        setting.code shouldEqual APIErrorCode.CLIENT_INVALID_AUTH_SCHEME
    }

}