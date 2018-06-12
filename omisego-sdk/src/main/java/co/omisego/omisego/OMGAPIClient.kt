package co.omisego.omisego

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.Wallet
import co.omisego.omisego.model.Setting
import co.omisego.omisego.model.User
import co.omisego.omisego.model.transaction.consumption.TransactionConsumptionActionParams
import co.omisego.omisego.model.transaction.consumption.TransactionConsumptionParams
import co.omisego.omisego.model.transaction.list.TransactionListParams
import co.omisego.omisego.model.transaction.request.TransactionRequestCreateParams
import co.omisego.omisego.model.transaction.request.TransactionRequestParams
import co.omisego.omisego.model.transaction.send.TransactionSendParam
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
 * val eWalletClient = EWalletClient.Builder {
 *      authenticationToken = YOUR_TOKEN
 *      apiKey = YOUR_API_KEY
 *      baseUrl = YOUR_BASE_URL
 * }.build()
 * </code>
 *
 * 2. Create an [OMGAPIClient] passing with [EWalletClient] as a parameter.
 *
 * For example,
 * <code>
 * val omgAPIClient = OMGAPIClient(eWalletClient)
 *
 * omgApiClient.listWallets().enqueue(object : OMGCallback<WalletList> {
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
     * Asynchronously send the request to transform the [User] corresponding to the provided authentication token.
     * if *success* the [OMGCallback<User>] will be invoked with the [User] parameter,
     * if *fail* [OMGCallback<User>] will be invoked with the [co.omisego.omisego.model.APIError] parameter.
     */
    fun getCurrentUser() = eWalletAPI.getCurrentUser()

    /**
     * Asynchronously send the request to get the global settings of the provider.
     * The global settings will contain a list of an available tokens to be used.
     *
     * if *success* the [OMGCallback<Setting>] callback will be invoked with [Setting] parameter,
     * if *fail* [OMGCallback<Setting>] will be invoked with the [co.omisego.omisego.model.APIError] parameter.
     */
    fun getSettings() = eWalletAPI.getSettings()

    /**
     * Asynchronously send the request to expire a user's authentication_token.
     * if *success* the [OMGCallback<String>] will be invoked with the empty [String] parameter,
     * if *fail* [OMGCallback<String>] will be invoked with the [co.omisego.omisego.model.APIError] parameter.
     */
    fun logout() = eWalletAPI.logout()

    /**
     * Asynchronously send the request to retrieve wallets of a user corresponding to the provided authentication token.
     * if *success* the [OMGCallback<WalletList>] will be invoked with the list of [Wallet] parameter,
     * if *fail* [OMGCallback<WalletList>] will be invoked with the [co.omisego.omisego.model.APIError] parameter.
     */
    fun listWallets() = eWalletAPI.listWallets()

    /**
     * Asynchronously get a paginated list of transactions of a user corresponding to the provided authentication token.
     * if *success* the [OMGCallback<PaginationList<Transaction>>] will be invoked with the [PaginationList<Transaction>],
     * if *fail* the [OMGCallback<PaginationList<Transaction>>] will be invoked with the [co.omisego.omisego.model.APIError] parameter.
     *
     * @param request A structure used to query a list of transactions for the current user
     */
    fun listTransactions(request: TransactionListParams) =
        eWalletAPI.listTransactions(request)

    /**
     * Asynchronously create a transaction request from the given [TransactionRequestCreateParams] object
     * if *success* the [OMGCallback<TransactionRequest>] will be invoked with the [co.omisego.omisego.model.transaction.request.TransactionRequest] parameter,
     * if *fail* the [OMGCallback<TransactionRequest>] will be invoked with the [co.omisego.omisego.model.APIError] parameter.
     *
     * @param request The [TransactionRequestCreateParams] object describing the transaction request to be made.
     */
    fun createTransactionRequest(request: TransactionRequestCreateParams) =
        eWalletAPI.createTransactionRequest(request)

    /**
     * Asynchronously retrieve a transaction request from its id
     * if *success* the [OMGCallback<TransactionRequest>] will be invoked with the [co.omisego.omisego.model.transaction.request.TransactionRequest] parameter,
     * if *fail* the [OMGCallback<TransactionRequest>] will be invoked with the [co.omisego.omisego.model.APIError] parameter.
     *
     * @param request The id of the TransactionRequest to be retrieved
     */
    fun retrieveTransactionRequest(request: TransactionRequestParams) =
        eWalletAPI.retrieveTransactionRequest(request)

    /**
     * Asynchronously consume a transaction request from the given [TransactionConsumptionParams] object
     * if *success* the [OMGCallback<TransactionConsumption>] will be invoked with the [co.omisego.omisego.model.transaction.consumption.TransactionConsumption] parameter,
     * if *fail* the [OMGCallback<TransactionConsumption>] will be invoked with the [co.omisego.omisego.model.APIError] parameter.
     *
     * @param request The TransactionConsumptionParams object describing the transaction request to be consumed.
     */
    fun consumeTransactionRequest(request: TransactionConsumptionParams) =
        eWalletAPI.consumeTransactionRequest(request)

    /**
     * Asynchronously approve the transaction consumption from the given [TransactionConsumptionActionParams] object
     * if *success* the [OMGCallback<TransactionConsumption>] will be invoked with the [co.omisego.omisego.model.transaction.consumption.TransactionConsumption] parameter,
     * if *fail* the [OMGCallback<TransactionConsumption>] will be invoked with the [co.omisego.omisego.model.APIError] parameter.
     *
     * @param request The TransactionConsumptionActionParams object containing the transaction consumption id to be approved.
     */
    fun approveTransactionConsumption(request: TransactionConsumptionActionParams) =
        eWalletAPI.approveTransactionConsumption(request)

    /**
     * Asynchronously reject the transaction consumption from the given [TransactionConsumptionActionParams] object
     * if *success* the [OMGCallback<TransactionConsumption>] will be invoked with the [co.omisego.omisego.model.transaction.consumption.TransactionConsumption] parameter,
     * if *fail* the [OMGCallback<TransactionConsumption>] will be invoked with the [co.omisego.omisego.model.APIError] parameter.
     *
     * @param request The TransactionConsumptionActionParams object containing the transaction consumption id to be rejected.
     */
    fun rejectTransactionConsumption(request: TransactionConsumptionActionParams) =
        eWalletAPI.rejectTransactionConsumption(request)

    /**
     * Send tokens to an address
     *
     * @param request The TransactionSendParams object to customize the transaction
     */
    fun transfer(request: TransactionSendParam) =
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
