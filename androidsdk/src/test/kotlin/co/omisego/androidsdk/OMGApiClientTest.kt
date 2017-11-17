package co.omisego.androidsdk

import co.omisego.androidsdk.models.*
import co.omisego.androidsdk.networks.DefaultHttpConnection
import co.omisego.androidsdk.networks.HttpConnection
import co.omisego.androidsdk.networks.RequestOptions
import co.omisego.androidsdk.networks.Requestor
import co.omisego.androidsdk.utils.APIErrorCode
import co.omisego.androidsdk.utils.ParseStrategy
import co.omisego.androidsdk.utils.Serializer
import junit.framework.Assert
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.amshove.kluent.*
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
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


@RunWith(MockitoJUnitRunner::class)
class OMGApiClientTest {
    private val TEST_AUTHORIZATION_TOKEN = "OMGClient MTQ4MnFOeFBleTdBNF9ycktrQU9iNGtBT1RzRDJIb0x5c1M3ZVExWmQzWTpVOExtQUZjbFFQWE9SV0RHcS1aLVNJNS0zQ3hKMllnMm0wRzdYaVJFNFRv"
    private val BASE_URL: String = "https://kubera.omisego.io/"
    private var secret: File? = null
    private lateinit var httpConnection: HttpConnection
    private lateinit var requestor: Requestor

    @Before
    fun setUp() {
        httpConnection = DefaultHttpConnection(BASE_URL)
        requestor = Requestor(httpConnection)

        val resourceUserURL = javaClass.classLoader.getResource("secret.json") // This is invisible because it's stored in local ("secret").
        secret = File(resourceUserURL.path)

        OMGApiClient.init(TEST_AUTHORIZATION_TOKEN, EmptyCoroutineContext)
    }

    private fun asyncLogin(): Deferred<String> {
        return async(EmptyCoroutineContext) {
            val authenticationServer = JSONObject(secret!!.readText()).getString("authenticationTokenServer")
            val job = requestor.asyncRequest("login", RequestOptions().apply {
                setHeaders("Authorization" to "OMGServer $authenticationServer",
                        "Accept" to "application/vnd.omisego.v1+json",
                        "Content-Type" to "application/vnd.omisego.v1+json")

                setBody("provider_user_id" to "user12345678")
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
        Assert.assertTrue(listAddress[0].balances.isNotEmpty())

        println(listAddress)

        listAddress
                .asSequence()
                .flatMap { it.balances.asSequence() }
                .forEach {
                    with(it) {
                        amount shouldNotBeLessThan 0.0
                        Assert.assertTrue(!mintedToken.symbol.isEmpty())
                        Assert.assertTrue(!mintedToken.name.isEmpty())
                        mintedToken.subUnitToUnit shouldBeGreaterThan 0.0
                    }
                }
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

        println(model.toString())
        model shouldBeInstanceOf Setting::class
        val listMintedToken = (model as Setting).mintedTokens
        for (mintedToken in listMintedToken) {
            assertTrue(mintedToken.subUnitToUnit.toString().isNotEmpty())
            assertTrue(mintedToken.name.isNotEmpty())
            assertTrue(mintedToken.symbol.isNotEmpty())
        }
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