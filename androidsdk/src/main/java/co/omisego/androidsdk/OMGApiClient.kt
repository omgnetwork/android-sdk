package co.omisego.androidsdk

import co.omisego.androidsdk.api.KuberaAPI
import co.omisego.androidsdk.models.Address
import co.omisego.androidsdk.models.General
import co.omisego.androidsdk.models.Setting
import co.omisego.androidsdk.models.User
import co.omisego.androidsdk.networks.DefaultHttpConnection
import co.omisego.androidsdk.networks.HttpConnection
import co.omisego.androidsdk.networks.RequestOptions
import co.omisego.androidsdk.networks.Requestor
import co.omisego.androidsdk.utils.APIErrorCode
import co.omisego.androidsdk.utils.ParseStrategy
import co.omisego.androidsdk.utils.ResponseProvider
import co.omisego.androidsdk.utils.Serializer
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.json.JSONObject
import kotlin.coroutines.experimental.CoroutineContext


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/10/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class OMGApiClient : KuberaAPI {
    private var authorization: String? = null
    private lateinit var main: CoroutineContext // main thread
    private val BASE_URL: String = "https://kubera.omisego.io/"
    private val httpConnection: HttpConnection by lazy { DefaultHttpConnection(BASE_URL) }
    private val requestor: Requestor by lazy { Requestor(httpConnection) }
    private val responseProvider: ResponseProvider by lazy { ResponseProvider() }

    class Builder(init: Builder.() -> Unit) {
        private var authorizationKey: String? = null
        private var context: CoroutineContext? = null

        fun setAuthorizationToken(authorizationToken: String) {
            authorizationKey = authorizationToken
        }

        fun setCoroutineContext(context: CoroutineContext) {
            this.context = context
        }

        fun build(): OMGApiClient {
            val apiClient = OMGApiClient()
            apiClient.authorization = authorizationKey
            apiClient.main = context ?: UI
            return apiClient
        }

        init {
            init()
        }
    }

    override fun getCurrentUser(callback: Callback<User>) {
        async(main) {
            process("me.get",
                    fail = {
                        callback.fail(response = responseProvider.failure(it))
                    },
                    success = {

                        callback.success(response = responseProvider.success(it, ParseStrategy.USER))
                    }
            )
        }
    }

    override fun logout(callback: Callback<String>) {
        val empty: (String) -> String = { "" }
        async(main) {
            process("logout",
                    fail = {
                        callback.fail(response = responseProvider.failure(it))
                    },
                    success = {
                        // Invalidate token
                        authorization = null
                        callback.success(response = responseProvider.success(it, empty))
                    }
            )
        }
    }


    override fun listBalances(callback: Callback<List<Address>>) {
        async(main) {
            process("me.list_balances",
                    fail = {
                        callback.fail(response = responseProvider.failure(it))
                    },
                    success = {
                        callback.success(response = responseProvider.success(it, ParseStrategy.LIST_BALANCES))
                    }
            )
        }
    }

    override fun getSettings(callback: Callback<Setting>) {
        async(main) {
            process("me.get_settings",
                    fail = {
                        callback.fail(response = responseProvider.failure(it))
                    },
                    success = {
                        callback.success(response = responseProvider.success(it, ParseStrategy.SETTING))
                    }
            )
        }
    }

    private fun process(endpoint: String, fail: (general: General) -> Unit, success: (general: General) -> Unit) = runBlocking {
        checkIfAuthorizationTokenSet(authorization)?.let { error ->
            fail(error)
            return@runBlocking
        }

        val job = requestor.asyncRequest(endpoint, RequestOptions().apply {
            setHeaders("Authorization" to (authorization ?: ""), "Accept" to "application/vnd.omisego.v1+json")
        })

        try {
            val response = job.await().response
            val general = Serializer(ParseStrategy.GENERAL).serialize(response!!)
            if (general.success) {
                success(general)
            } else {
                fail(general)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkIfAuthorizationTokenSet(authorizationToken: String?): General? {
        if (authorizationToken == null || authorizationToken.isEmpty()) {
            val failJsonObject = JSONObject()
            failJsonObject.put("code", APIErrorCode.CLIENT_INVALID_AUTH_SCHEME)
            failJsonObject.put("description", "OMGApiClient has not been initialized with the correct authorization token. Please call init(authorizationToken) first.")
            return General("1", false, failJsonObject)
        }
        return null
    }
}