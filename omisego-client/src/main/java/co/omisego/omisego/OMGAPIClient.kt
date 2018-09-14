package co.omisego.omisego

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.User
import co.omisego.omisego.model.params.LoginParams
import co.omisego.omisego.model.params.SignUpParams
import co.omisego.omisego.model.transaction.consumption.TransactionConsumptionActionParams
import co.omisego.omisego.model.transaction.consumption.TransactionConsumptionParams
import co.omisego.omisego.model.transaction.list.TransactionListParams
import co.omisego.omisego.model.transaction.request.TransactionRequestCreateParams
import co.omisego.omisego.model.transaction.request.TransactionRequestParams
import co.omisego.omisego.model.transaction.send.TransactionCreateParams
import co.omisego.omisego.network.ewallet.EWalletClient

/**
 * The class OMGAPIClient represents an object that knows how to interact with OmiseGO API.
 *
 * To initialize [OMGAPIClient], the following steps should be taken.
 *
 * 1. Create the [EWalletClient] instance using the [EWalletClient.Builder]
 *
 * For example,
 * <code>
 * val config = ClientConfiguration(
 *      authenticationToken = YOUR_TOKEN
 *      apiKey = YOUR_API_KEY
 *      baseUrl = YOUR_BASE_URL
 * )
 *
 * val eWalletClient = EWalletClient.Builder {
 *      clientConfiguration = config
 * }.build()
 * </code>
 *
 * 2. Create an [OMGAPIClient] passing with [EWalletClient] as a parameter.
 *
 * For example,
 * <code>
 * val client = OMGAPIClient(eWalletClient)
 *
 * omgApiClient.getWallets().enqueue(object : OMGCallback<WalletList> {
 *      override fun success(response: OMGResponse<WalletList>) {
 *          // Handle success
 *      }
 *
 *      override fun fail(response: OMGResponse<APIError>) {
 *          // Handle error
 *      }
 * })
 * </code>
 *
 * 3. You're done!
 */
class OMGAPIClient(private val eWalletClient: EWalletClient) {
    private val eWalletAPI
        get() = eWalletClient.eWalletAPI

    /**
     * Asynchronously send the request to login to get an authentication token.
     * if *success* the `success` function will be invoked with the [OMGResponse<UserAuthenticationToken>] parameter.
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param params A set of parameters used for login
     */
    fun login(params: LoginParams) = eWalletAPI.login(params)

    /**
     * Asynchronously send the request to signup
     * if *success* the `success` function will be invoked with the [OMGResponse<User>] parameter.
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param params A set of parameters used for signup
     */
    fun signup(params: SignUpParams) = eWalletAPI.signup(params)

    /**
     * Asynchronously send the request to expire a user's authentication_token.
     * if *success* the `success` function will be invoked with the [OMGResponse<String>] parameter.
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     */
    fun logout() = eWalletAPI.logout()

    /**
     * Asynchronously send the request to transform the [User] corresponding to the provided authentication token.
     * if *success* the `success` function will be invoked with the [OMGResponse<User>] parameter.
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     */
    fun getCurrentUser() = eWalletAPI.getCurrentUser()

    /**
     * Asynchronously send the request to get the global settings of the provider.
     * The global settings will contain a list of an available tokens to be used.
     *
     * if *success* the `success` function will be invoked with the [OMGResponse<Setting>] parameter.
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     */
    fun getSettings() = eWalletAPI.getSettings()

    /**
     * Asynchronously send the request to retrieve wallets of a user corresponding to the provided authentication token.
     * if *success* the `success` function will be invoked with the [OMGResponse<WalletList>] parameter.
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     */
    fun getWallets() = eWalletAPI.getWallets()

    /**
     * Asynchronously get a paginated list of transactions of a user corresponding to the provided authentication token.
     * if *success* the `success` function will be invoked with the [OMGResponse<PaginationList<Transaction>>] parameter.
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param request A structure used to query a list of transactions for the current user
     */
    fun getTransactions(request: TransactionListParams) =
        eWalletAPI.getTransactions(request)

    /**
     * Asynchronously create a transaction request from the given [TransactionRequestCreateParams] object.
     * if *success* the `success` function will be invoked with the [OMGResponse<TransactionRequest>] parameter.
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param request The [TransactionRequestCreateParams] object describing the transaction request to be made.
     */
    fun createTransactionRequest(request: TransactionRequestCreateParams) =
        eWalletAPI.createTransactionRequest(request)

    /**
     * Asynchronously retrieve a transaction request from its formattedId.
     * if *success* the `success` function will be invoked with the [OMGResponse<TransactionRequest>] parameter.
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param request The formattedId of the TransactionRequest to be retrieved
     */
    fun retrieveTransactionRequest(request: TransactionRequestParams) =
        eWalletAPI.retrieveTransactionRequest(request)

    /**
     * Asynchronously consume a transaction request from the given [TransactionConsumptionParams] object.
     * if *success* the `success` function will be invoked with the [OMGResponse<TransactionConsumption>] parameter.
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param request The TransactionConsumptionParams object describing the transaction request to be consumed.
     */
    fun consumeTransactionRequest(request: TransactionConsumptionParams) =
        eWalletAPI.consumeTransactionRequest(request)

    /**
     * Asynchronously approve the transaction consumption from the given [TransactionConsumptionActionParams] object.
     * if *success* the `success` function will be invoked with the [OMGResponse<TransactionConsumption>] parameter.
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param request The TransactionConsumptionActionParams object containing the transaction consumption id to be approved.
     */
    fun approveTransactionConsumption(request: TransactionConsumptionActionParams) =
        eWalletAPI.approveTransactionConsumption(request)

    /**
     * Asynchronously reject the transaction consumption from the given [TransactionConsumptionActionParams] object.
     * if *success* the `success` function will be invoked with the [OMGResponse<TransactionConsumption>] parameter.
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param request The TransactionConsumptionActionParams object containing the transaction consumption id to be rejected.
     */
    fun rejectTransactionConsumption(request: TransactionConsumptionActionParams) =
        eWalletAPI.rejectTransactionConsumption(request)

    /**
     * Send tokens to an address.
     * if *success* the `success` function will be invoked with the [OMGResponse<Transaction>] parameter.
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param request The TransactionCreateParams object to customize the transaction
     */
    fun createTransaction(request: TransactionCreateParams) =
        eWalletAPI.transfer(request)

    /**
     * Set new [authenticationToken].
     *
     * @param authenticationToken An authentication token to replace the old value.
     */
    fun setAuthenticationTokenHeader(authenticationToken: String) {
        eWalletClient.header.setHeader(authenticationToken)
    }
}
