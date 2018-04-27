package co.omisego.omisego

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.Address
import co.omisego.omisego.model.Setting
import co.omisego.omisego.model.User
import co.omisego.omisego.model.transaction.consumption.TransactionConsumptionParams
import co.omisego.omisego.model.transaction.list.TransactionListParams
import co.omisego.omisego.model.transaction.request.TransactionRequestCreateParams
import co.omisego.omisego.model.transaction.request.TransactionRequestParams
import co.omisego.omisego.network.ewallet.EWalletClient

/**
 * The class OMGAPIClient represents an object that knows how to interact with OmiseGO API.
 *
 * Create instances using [OMGAPIClient.Builder] and pass your implementation of [OMGCallback<T>] interface
 * to generate an implementation
 *
 * For example,
 * <code>
 * val eWalletClient = EWalletClient.Builder {
 *      authenticationToken = YOUR_TOKEN
 *      baseUrl = YOUR_BASE_URL
 * }.build()
 *
 * val omgAPIClient = OMGAPIClient(eWalletClient)
 *
 * omgApiClient.listBalances().enqueue(object : OMGCallback<BalanceList> {
 *      override fun success(response: OMGResponse<BalanceList>) {
 *          // Handle success
 *      }
 *
 *      override fun fail(response: OMGResponse<APIError>) {
 *          // Handle error
 *      }
 * })
 * </code>
 *
 */
class OMGAPIClient(private val eWalletClient: EWalletClient) {
    private val eWalletAPI
        get() = eWalletClient.eWalletAPI

    /**
     * Asynchronously send the request to transform the [User] corresponding to the provided authentication token.
     * if *success* the [callback] will be invoked with the [User] parameter,
     * if *fail* [callback] will be invoked with the [co.omisego.omisego.models.ApiError] parameter.
     *
     * @param callback A callback to receive the response from server.
     */
    fun getCurrentUser() = eWalletAPI.getCurrentUser()

    /**
     * Asynchronously send the request to transform the global settings.
     * if *success* the [callback] will be invoked with [Setting] parameter,
     * if *fail* [callback] will be invoked with the [co.omisego.omisego.models.ApiError] parameter.
     *
     * @param callback A callback to receive the response from server.
     */
    fun getSettings() = eWalletAPI.getSettings()

    /**
     * Asynchronously send the request to expire a user's authentication_token.
     * if *success* the [callback] will be invoked with the empty [String] parameter,
     * if *fail* [callback] will be invoked with the [co.omisego.omisego.models.ApiError] parameter.
     *
     * @param callback A callback to receive the response from server.
     */
    fun logout() = eWalletAPI.logout()

    /**
     * Asynchronously send the request to transform the balances of a user corresponding to the provided authentication token.
     * if *success* the [callback] will be invoked with the list of [Address] parameter,
     * if *fail* [callback] will be invoked with the [co.omisego.omisego.models.ApiError] parameter.
     *
     * @param callback A callback to receive the response from server.
     */
    fun listBalances() = eWalletAPI.listBalances()

    /**
     * Get a paginated list of transaction for the current user
     *
     * @param request A structure used to query a list of transactions for the current user
     */
    fun listTransactions(request: TransactionListParams) =
        eWalletAPI.listTransactions(request)

    /**
     * Generate a transaction request from the given [TransactionRequestCreateParams] object
     *
     * @param request The [TransactionRequestCreateParams] object describing the transaction request to be made.
     */
    fun createTransactionRequest(request: TransactionRequestCreateParams) =
        eWalletAPI.createTransactionRequest(request)

    /**
     * Retrieve a transaction request from its id
     *
     * @param request The id of the TransactionRequest to be retrieved
     */
    fun retrieveTransactionRequest(request: TransactionRequestParams) =
        eWalletAPI.retrieveTransactionRequest(request)

    /**
     * Consume a transaction request from the given TransactionConsumptionParams object
     *
     * @param request The TransactionConsumptionParams object describing the transaction request to be consumed.
     */
    fun consumeTransactionRequest(request: TransactionConsumptionParams) =
        eWalletAPI.consumeTransactionRequest(request)

    /**
     * Set new [authenticationToken].
     *
     * @param authenticationToken An authentication token to replace the old value.
     */
    fun setAuthenticationTokenHeader(authenticationToken: String) {
        eWalletClient.header.setHeader(authenticationToken)
    }
}
