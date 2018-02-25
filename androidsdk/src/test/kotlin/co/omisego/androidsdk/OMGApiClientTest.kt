package co.omisego.androidsdk

import co.omisego.androidsdk.extensions.bd
import co.omisego.androidsdk.models.*
import co.omisego.androidsdk.networks.DefaultHttpConnection
import co.omisego.androidsdk.networks.HttpConnection
import co.omisego.androidsdk.networks.RequestOptions
import co.omisego.androidsdk.networks.Requestor
import co.omisego.androidsdk.utils.ErrorCode
import co.omisego.androidsdk.utils.OMGEncryptionHelper
import co.omisego.androidsdk.utils.ParseStrategy
import co.omisego.androidsdk.utils.Serializer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.whenever
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
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.File
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
    private lateinit var baseURL: String
    private val secretFileName: String = "secret.json" // Replace your secret file here
    private val secret: JSONObject by lazy { loadSecretFile(secretFileName) }
    private lateinit var httpConnection: HttpConnection
    private lateinit var requestor: Requestor
    private lateinit var omgApiClient: OMGApiClient
    private lateinit var mAuthtoken: String
    @Mock
    private lateinit var mockRequestor: Requestor

    @Before
    fun setUp() {
        baseURL = secret.getString("base_url")
        assertKeyIsNotEmpty()
        httpConnection = DefaultHttpConnection(baseURL)
        requestor = Requestor(httpConnection)

        val auth = OMGEncryptionHelper.encryptBase64(
                secret.getString("api_key"),
                secret.getString("auth_token")
        )
        mAuthtoken = "OMGClient $auth"

        omgApiClient = OMGApiClient.Builder {
            setAuthorizationToken(mAuthtoken)
            setCoroutineContext(EmptyCoroutineContext)
        }.build()
    }

    private fun asyncLogin(): Deferred<String> {
        return async(EmptyCoroutineContext) {
            val authorization = OMGEncryptionHelper.encryptBase64(
                    secret.getString("access_key"),
                    secret.getString("secret_key")
            )

            val job = requestor.asyncRequest("login", RequestOptions().apply {
                setHeaders("Authorization" to "OMGServer $authorization",
                        "Accept" to "application/vnd.omisego.v1+json",
                        "Content-Type" to "application/vnd.omisego.v1+json")

                setBody("provider_user_id" to "provider_user_id01")
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
        omgApiClient.getCurrentUser(object : Callback<User> {
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
        omgApiClient = OMGApiClient.Builder {
            setAuthorizationToken("wrong")
            setCoroutineContext(EmptyCoroutineContext)
        }.build()

        // Just don't care about thread here. Because in android, will work properly.
        // Action
        omgApiClient.getCurrentUser(object : Callback<User> {
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
        user.code shouldEqual ErrorCode.CLIENT_INVALID_AUTH_SCHEME
    }

    @Test
    fun `logout success`() = runBlocking {
        // Arrange
        secret shouldNotBe null

        var actualResponse: Response<Any>? = null

        // Retrieve token from login API
        val token = asyncLogin().await()

        // Retrieve token from local file.
        val apiKey = secret.getString("api_key")

        val authenticationToken = "OMGClient ${OMGEncryptionHelper.encryptBase64(apiKey, token)}"

        // Action
        omgApiClient = OMGApiClient.Builder {
            setAuthorizationToken(authenticationToken)
            setCoroutineContext(EmptyCoroutineContext)
        }.build()

        omgApiClient.logout(object : Callback<String> {
            override fun success(response: Response<String>) {
                actualResponse = response
            }

            override fun fail(response: Response<ApiError>) {
                actualResponse = response
            }
        })

        delay(3000)

        // Assert
        actualResponse shouldNotBe null
        actualResponse?.success shouldEqual true

        // Try to use the same token
        omgApiClient = OMGApiClient.Builder {
            setAuthorizationToken(authenticationToken)
            setCoroutineContext(EmptyCoroutineContext)
        }.build()

        omgApiClient.logout(object : Callback<String> {
            override fun success(response: Response<String>) {
                actualResponse = response
            }

            override fun fail(response: Response<ApiError>) {
                actualResponse = response
            }
        })

        delay(3000)

        // Assert that it should fail because token has been invalidated
        actualResponse shouldNotBe null
        actualResponse?.success shouldEqual false
        actualResponse?.data shouldBeInstanceOf ApiError::class
        val error = actualResponse?.data as ApiError
        error.code shouldEqual ErrorCode.USER_ACCESS_TOKEN_EXPIRED

    }

    @Test
    fun `list balances success`() = runBlocking {
        // Arrange
        var actualResponse: Response<Any>? = null

        // Just don't care about thread here. Because in android, will work properly.
        // Action
        omgApiClient.listBalances(object : Callback<List<Address>> {
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

        listAddress
                .asSequence()
                .flatMap { it.balances.asSequence() }
                .forEach {
                    with(it) {
                        Assert.assertTrue(!mintedToken.symbol.isEmpty())
                        Assert.assertTrue(!mintedToken.name.isEmpty())
                        Assert.assertTrue(!mintedToken.id.isEmpty())
                        Assert.assertTrue(mintedToken.subUnitToUnit > 0.0.bd)
                    }
                }
    }

    @Test
    fun `list balances should fail because wrong token given`() = runBlocking {
        // Arrange
        var actualResponse: Response<Any>? = null
        omgApiClient = OMGApiClient.Builder {
            setAuthorizationToken("wrong")
            setCoroutineContext(EmptyCoroutineContext)
        }.build()

        // Just don't care about thread here. Because in android, will work properly.
        // Action
        omgApiClient.listBalances(object : Callback<List<Address>> {
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
        assertTrue(model !is List<*>)

        val setting = model as ApiError
        setting.code shouldEqual ErrorCode.CLIENT_INVALID_AUTH_SCHEME
    }

    @Test
    fun `get settings success`() = runBlocking {
        // Arrange
        var actualResponse: Response<Any>? = null

        // Just don't care about thread here. Because in android, will work properly.
        // Action
        omgApiClient.getSettings(object : Callback<Setting> {
            override fun success(response: Response<Setting>) {
                actualResponse = response
            }

            override fun fail(response: Response<ApiError>) {
                actualResponse = response
            }
        })

        delay(3000)

        println(actualResponse)

        // Assert
        actualResponse shouldNotBe null
        val model = actualResponse!!.data
        model shouldNotBeInstanceOf ApiError::class

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
        val omgApiClient = OMGApiClient.Builder {
            setAuthorizationToken("wrong")
            setCoroutineContext(EmptyCoroutineContext)
        }.build()

        // Just don't care about thread here. Because in android, will work properly.
        // Action
        omgApiClient.getSettings(object : Callback<Setting> {
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
        setting.code shouldEqual ErrorCode.CLIENT_INVALID_AUTH_SCHEME
    }

    @Test
    fun `sdk network failed should show error correctly`() = runBlocking {
        // Arrange
        val errorDescription = "The rat bit the internet cable"
        val mockRawData = RawData(errorDescription, false, ErrorCode.SDK_NETWORK_ERROR)
        whenever(mockRequestor.asyncRequest(eq("me.get_settings"), any()))
                .thenReturn(async { return@async mockRawData })
        val mockApiClient = OMGApiClient.Builder {
            setAuthorizationToken(mAuthtoken)
            setCoroutineContext(EmptyCoroutineContext)
            setRequestor(mockRequestor)
        }.build()

        var actualResponse: Response<Any>? = null

        // Action
        mockApiClient.getSettings(object : Callback<Setting> {
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
        model shouldBeInstanceOf ApiError::class

        val apiError = model as ApiError
        apiError.code shouldEqual ErrorCode.SDK_NETWORK_ERROR
        apiError.description shouldEqual errorDescription

    }

    private fun loadSecretFile(filename: String): JSONObject {
        val resourceUserURL = javaClass.classLoader.getResource(filename) // This is invisible because it's stored in local ("secret").

        return try {
            val secretFile = File(resourceUserURL.path)
            JSONObject(secretFile.readText())
        } catch (e: IllegalStateException) {
            throw IllegalStateException("Please create the file $filename. See the file secret.example.json for the reference.")
        }
    }

    private fun assertKeyIsNotEmpty() {
        Assert.assertTrue("Assign your baseURL before run the test", baseURL != "")
    }
}
