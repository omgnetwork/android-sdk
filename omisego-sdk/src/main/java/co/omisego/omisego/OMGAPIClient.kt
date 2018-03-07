package co.omisego.omisego

import co.omisego.omisego.custom.Callback
import co.omisego.omisego.custom.CallbackManager
import co.omisego.omisego.custom.Serializer
import co.omisego.omisego.model.*
import co.omisego.omisego.network.ewallet.EWalletClient
import com.google.gson.reflect.TypeToken


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */


/**
 * The class OMGApiClient represents an object that knows how to interact with OmiseGO API.
 *
 * Create instances using [OMGApiClient.Builder] and pass your implementation of [Callback<T>] interface
 * to generate an implementation
 *
 * For example,
 * <code>
 * val omgApiClient = OMGApiClient.Builder {
 *      setAuthorizationToken(YOUR_TOKEN)
 * }.build()
 *
 * omgApiClient.getCurrentUser(object : Callback<User> {
 *      override fun success(response: OMGResponse<User>) {
 *          // Do something
 *      }
 *
 *      override fun fail(response: OMGResponse<ApiError>) {
 *          // Handle fail case properly
 *      }
 * })
 * </code>
 *
 */
class OMGAPIClient(private val eWalletClient: EWalletClient) {
    private val serializer = Serializer()

    /**
     * Asynchronously send the request to transform the [User] corresponding to the provided authentication token.
     * if *success* the [callback] will be invoked with the [User] parameter,
     * if *fail* [callback] will be invoked with the [co.omisego.omisego.models.ApiError] parameter.
     *
     * @param callback A callback to receive the response from server.
     */
    fun getCurrentUser(callback: Callback<User>) {
        val type = object : TypeToken<OMGResponse<User>>() {}.type
        val callbackManager = CallbackManager<User>(serializer, type)
        eWalletClient.eWalletAPI.getCurrentUser().enqueue(callbackManager.transform(callback))
    }

    /**
     * Asynchronously send the request to transform the global settings.
     * if *success* the [callback] will be invoked with [Setting] parameter,
     * if *fail* [callback] will be invoked with the [co.omisego.omisego.models.ApiError] parameter.
     *
     * @param callback A callback to receive the response from server.
     */
    fun getSettings(callback: Callback<Setting>) {
        val type = object : TypeToken<OMGResponse<Setting>>() {}.type
        val callbackManager = CallbackManager<Setting>(serializer, type)
        eWalletClient.eWalletAPI.getSettings().enqueue(callbackManager.transform(callback))
    }

    /**
     * Asynchronously send the request to expire a user's authentication_token.
     * if *success* the [callback] will be invoked with the empty [String] parameter,
     * if *fail* [callback] will be invoked with the [co.omisego.omisego.models.ApiError] parameter.
     *
     * @param callback A callback to receive the response from server.
     */
    fun logout(callback: Callback<Logout>) {
        val type = object : TypeToken<OMGResponse<Logout>>() {}.type
        val callbackManager = CallbackManager<Logout>(serializer, type)
        eWalletClient.eWalletAPI.logout().enqueue(callbackManager.transform(callback))
    }

    /**
     * Asynchronously send the request to transform the balances of a user corresponding to the provided authentication token.
     * if *success* the [callback] will be invoked with the list of [Address] parameter,
     * if *fail* [callback] will be invoked with the [co.omisego.omisego.models.ApiError] parameter.
     *
     * @param callback A callback to receive the response from server.
     */
    fun listBalances(callback: Callback<BalanceList>) {
        val type = object : TypeToken<OMGResponse<BalanceList>>() {}.type
        val callbackManager = CallbackManager<BalanceList>(serializer, type)
        eWalletClient.eWalletAPI.listBalance().enqueue(callbackManager.transform(callback))
    }

}