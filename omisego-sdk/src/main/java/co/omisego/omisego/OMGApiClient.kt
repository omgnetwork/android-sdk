//package co.omisego.omisego
//
//import android.util.Log
//import co.omisego.omisego.api.KuberaAPI
//import co.omisego.omisego.models.Address
//import co.omisego.omisego.models.General
//import co.omisego.omisego.models.Setting
//import co.omisego.omisego.models.User
//import co.omisego.omisego.constants.ErrorCode
//import kotlinx.coroutines.experimental.android.UI
//import kotlinx.coroutines.experimental.async
//import kotlinx.coroutines.experimental.runBlocking
//import org.json.JSONObject
//import kotlin.coroutines.experimental.CoroutineContext
//
//
///**
// * OmiseGO
// *
// * Created by Phuchit Sirimongkolsathien on 11/10/2017 AD.
// * Copyright © 2017 OmiseGO. All rights reserved.
// */
//
///**
// * The class OMGApiClient represents an object that knows how to interact with OmiseGO API.
// *
// * Create instances using [OMGApiClient.Builder] and pass your implementation of [Callback<T>] interface
// * to generate an implementation
// *
// * For example,
// * <code>
// * val omgApiClient = OMGApiClient.Builder {
// *      setAuthorizationToken(YOUR_TOKEN)
// * }.build()
// *
// * omgApiClient.getCurrentUser(object : Callback<User> {
// *      override fun success(response: OMGResponse<User>) {
// *          // Do something
// *      }
// *
// *      override fun fail(response: OMGResponse<ApiError>) {
// *          // Handle fail case properly
// *      }
// * })
// * </code>
// *
// */
//class OMGApiClient : KuberaAPI {
//    private var authorization: String? = null
//    private lateinit var main: CoroutineContext // main thread
//    private var baseUrl: String? = null
//    private lateinit var httpConnection: HttpConnection
//    private lateinit var requestor: Requestor
//    private val responseProvider: ResponseProvider by lazy { ResponseProvider() }
//
//
//    /**
//     * Build a new [OMGApiClient].
//     * Calling [Builder.setAuthorizationToken] is required before calling [Builder.build].
//     * All other methods are not necessary.
//     *
//     * @receiver A [Builder]'s methods.
//     */
//    class Builder(init: Builder.() -> Unit) {
//        private var authenticationToken: String? = null
//        private var context: CoroutineContext? = null
//        private var baseURL: String? = null
//        private var requestor: Requestor? = null
//
//        /**
//         * Set the API [authorizationToken].
//         * The [authorizationToken] should be "OMG Base64(api_key:authentication_token)"
//         *
//         * @param authorizationToken token sent in the headers of the request for authentication.
//         */
//        fun setAuthorizationToken(authorizationToken: String) {
//            this.authenticationToken = authorizationToken
//        }
//
//        /**
//         * Set the API [baseURL].
//         *
//         * @param baseURL is the URL of the OmiseGO Wallet API.
//         */
//        fun setBaseURL(baseURL: String) {
//            this.baseURL = baseURL
//        }
//
//        /**
//         * For testing purpose
//         *
//         * @param context the mocked CoroutineContext, usually [kotlin.coroutines.experimental.EmptyCoroutineContext] for testing.
//         */
//        fun setCoroutineContext(context: CoroutineContext) {
//            this.context = context
//        }
//
//        /**
//         * For testing purpose
//         *
//         * @param requestor the mocked [Requestor]
//         */
//        fun setRequestor(requestor: Requestor) {
//            this.requestor = requestor
//        }
//
//        /**
//         * Create the [OMGApiClient] instance using the configured values.
//         * Note: Calling [Builder.setAuthorizationToken] is required before calling this.
//         */
//        fun build(): OMGApiClient {
//            checkBaseURLSet()
//            val apiClient = OMGApiClient()
//            apiClient.authorization = authenticationToken
//            apiClient.main = context ?: UI
//            apiClient.baseUrl = baseURL
//            apiClient.httpConnection = DefaultHttpConnection(baseURL!!)
//            apiClient.requestor = Requestor(apiClient.httpConnection)
//            this.requestor?.let {
//                apiClient.requestor = it
//            }
//            return apiClient
//        }
//
//        private fun checkBaseURLSet() {
//            if (baseURL == null) throw IllegalStateException("baseURL should be set before build!")
//        }
//
//        init {
//            init()
//        }
//    }
//
//    /**
//     * Asynchronously send the request to transform the [User] corresponding to the provided authentication token.
//     * if *success* the [callback] will be invoked with the [User] parameter,
//     * if *fail* [callback] will be invoked with the [co.omisego.omisego.models.ApiError] parameter.
//     *
//     * @param callback A callback to receive the response from server.
//     */
//    override fun getCurrentUser(callback: Callback<User>) {
//        async(main) {
//            process("me.transform",
//                    fail = {
//                        callback.fail(response = responseProvider.failure(it))
//                    },
//                    success = {
//                        callback.success(response = responseProvider.success(it, ParseStrategy.USER))
//                    }
//            )
//        }
//    }
//
//    /**
//     * Asynchronously send the request to expire a user's authentication_token.
//     * if *success* the [callback] will be invoked with the empty [String] parameter,
//     * if *fail* [callback] will be invoked with the [co.omisego.omisego.models.ApiError] parameter.
//     *
//     * @param callback A callback to receive the response from server.
//     */
//    override fun logout(callback: Callback<String>) {
//        val empty: (String) -> String = { "" }
//        async(main) {
//            process("logout",
//                    fail = {
//                        callback.fail(response = responseProvider.failure(it))
//                    },
//                    success = {
//                        // Invalidate token
//                        authorization = null
//                        callback.success(response = responseProvider.success(it, empty))
//                    }
//            )
//        }
//    }
//
//    /**
//     * Asynchronously send the request to transform the balances of a user corresponding to the provided authentication token.
//     * if *success* the [callback] will be invoked with the list of [Address] parameter,
//     * if *fail* [callback] will be invoked with the [co.omisego.omisego.models.ApiError] parameter.
//     *
//     * @param callback A callback to receive the response from server.
//     */
//    override fun listBalances(callback: Callback<List<Address>>) {
//        async(main) {
//            process("me.list_balances",
//                    fail = {
//                        callback.fail(response = responseProvider.failure(it))
//                    },
//                    success = {
//                        callback.success(response = responseProvider.success(it, ParseStrategy.LIST_BALANCES))
//                    }
//            )
//        }
//    }
//
//    /**
//     * Asynchronously send the request to transform the global settings.
//     * if *success* the [callback] will be invoked with [Setting] parameter,
//     * if *fail* [callback] will be invoked with the [co.omisego.omisego.models.ApiError] parameter.
//     *
//     * @param callback A callback to receive the response from server.
//     */
//    override fun getSettings(callback: Callback<Setting>) {
//        async(main) {
//            process("me.get_settings",
//                    fail = {
//                        callback.fail(response = responseProvider.failure(it))
//                    },
//                    success = {
//                        callback.success(response = responseProvider.success(it, ParseStrategy.SETTING))
//                    }
//            )
//        }
//    }
//
//    private fun process(endpoint: String, fail: (general: General) -> Unit, success: (general: General) -> Unit) = runBlocking {
//        checkIfAuthorizationTokenSet(authorization)?.let { error ->
//            fail.invoke(error)
//            return@runBlocking
//        }
//
//        val job = requestor.asyncRequest(endpoint, RequestOptions().apply {
//            setHeaders(
//                    "Authorization" to (authorization ?: ""),
//                    "Accept" to "application/vnd.omisego.v1+json"
//            )
//        })
//
//        try {
//            val rawData = job.await()
//            if (!rawData.success) {
//                val error = JSONObject().apply {
//                    put("code", rawData.errorCode)
//                    put("description", rawData.response)
//                }
//                val jsonObject = JSONObject().put("data", error)
//                fail.invoke(General(Versions.EWALLET_API, false, jsonObject))
//                return@runBlocking
//            }
//
//            val general = Serializer(ParseStrategy.GENERAL).serialize(rawData.response!!)
//
//            try {
//                when {
//                    !general.success -> fail.invoke(general)
//                    else -> success.invoke(general)
//                }
//            } catch (e: Exception) {
//                // This error is happen inside the code of the sdk user.
//                Log.e("OMGApiClient", e.message)
//                e.printStackTrace()
//            }
//
//        } catch (e: Exception) {
//            val error = JSONObject().apply {
//                put("code", ErrorCode.SDK_PARSE_ERROR)
//                put("description", e.message)
//            }
//
//            val jsonObject = JSONObject().apply {
//                put("data", error)
//            }
//            val general = General(Versions.EWALLET_API, false, jsonObject)
//            fail.invoke(general)
//            e.printStackTrace()
//        }
//    }
//
//    private fun checkIfAuthorizationTokenSet(authorizationToken: String?): General? {
//        if (authorizationToken == null || authorizationToken.isEmpty()) {
//            val failJsonObject = JSONObject()
//            failJsonObject.put("code", ErrorCode.CLIENT_INVALID_AUTH_SCHEME)
//            failJsonObject.put("description", "OMGApiClient has not been initialized with the correct authorization token. Please call init(authorizationToken) first.")
//            return General(Versions.EWALLET_API, false, failJsonObject)
//        }
//        return null
//    }
//}