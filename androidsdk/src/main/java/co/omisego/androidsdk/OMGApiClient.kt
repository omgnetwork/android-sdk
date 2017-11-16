package co.omisego.androidsdk

import co.omisego.androidsdk.api.KuberaAPI
import co.omisego.androidsdk.models.*
import co.omisego.androidsdk.networks.RequestOptions
import co.omisego.androidsdk.networks.Requestor
import co.omisego.androidsdk.utils.APIErrorCode
import co.omisego.androidsdk.utils.ParseStrategy
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

object OMGApiClient : KuberaAPI {
    private var authorization: String? = null


    @JvmOverloads
    fun init(authorization: String, ui: CoroutineContext = UI) {
        this.authorization = authorization
        this.main = ui
    }

    // Main thread
    private lateinit var main: CoroutineContext

    override fun getCurrentUser(callback: Callback<User>) {
        async(main) {
            process("me.get",
                    fail = {
                        callback.fail(response = provideCommonFailure(it))
                    },
                    success = {
                        callback.success(response = provideCommonSuccess(it, ParseStrategy.USER))
                    }
            )
        }
    }

    override fun logout(callback: Callback<String>) {
        val empty: (String) -> String = { "" }
        async(main) {
            process("logout",
                    fail = {
                        callback.fail(response = provideCommonFailure(it))
                    },
                    success = {
                        // Invalidate token
                        authorization = null
                        callback.success(response = provideCommonSuccess(it, empty))
                    }
            )
        }
    }


    override fun listBalances(callback: Callback<List<Address>>) {
        async(main) {
            process("me.list_balances",
                    fail = {
                        callback.fail(response = provideCommonFailure(it))
                    },
                    success = {
                        callback.success(response = provideCommonSuccess(it, ParseStrategy.LIST_BALANCES))
                    }
            )
        }
    }

    override fun getSettings(callback: Callback<Setting>) {
        async(main) {
            process("me.get_settings",
                    fail = {
                        callback.fail(response = provideCommonFailure(it))
                    },
                    success = {
                        callback.success(response = provideCommonSuccess(it, ParseStrategy.SETTING))
                    }
            )
        }
    }

    private fun process(endpoint: String, fail: (general: General) -> Unit, success: (general: General) -> Unit) = runBlocking {
        checkIfAuthorizationTokenSet(authorization)?.let { error ->
            fail(error)
            return@runBlocking
        }

        val job = Requestor.asyncRequest(BASE_URL + endpoint, RequestOptions().apply {
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
            println("Fail")
            e.printStackTrace()
        }
    }

    private fun <T> provideCommonSuccess(general: General, handler: (String) -> T): Response<T> {
        return Response(general.version,
                true,
                Serializer(handler).serialize(general.data.toString()))
    }

    private fun provideCommonFailure(general: General): Response<ApiError> {
        return Response(general.version,
                false,
                Serializer(ParseStrategy.API_ERROR).serialize(general.data.toString()))
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