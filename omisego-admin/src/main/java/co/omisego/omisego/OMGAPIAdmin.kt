package co.omisego.omisego

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.params.AccountListParams
import co.omisego.omisego.model.params.AccountWalletListParams
import co.omisego.omisego.model.params.LoginParams
import co.omisego.omisego.model.params.TokenListParams
import co.omisego.omisego.model.params.TransactionConsumptionActionParams
import co.omisego.omisego.model.params.admin.TransactionListParams
import co.omisego.omisego.model.params.TransactionRequestParams
import co.omisego.omisego.model.params.UserWalletListParams
import co.omisego.omisego.model.params.WalletParams
import co.omisego.omisego.model.params.admin.TransactionConsumptionParams
import co.omisego.omisego.model.params.admin.TransactionCreateParams
import co.omisego.omisego.model.params.admin.TransactionRequestCreateParams
import co.omisego.omisego.network.ewallet.EWalletAdmin

/**
 * The class OMGAPIAdmin represents an object that knows how to interact with OmiseGO API.
 *
 * To initialize [OMGAPIAdmin], the following steps should be taken.
 *
 * 1. Create the [EWalletAdmin] instance using the [EWalletAdmin.Builder]
 *
 * For example,
 * <code>
 * val config = AdminConfiguration(
 *      authenticationToken = YOUR_TOKEN
 *      userId = YOUR_USER_ID
 *      baseUrl = YOUR_BASE_URL
 * )
 *
 * val eWalletAdmin = EWalletAdmin.Builder {
 *      clientConfiguration = config
 * }.build()
 * </code>
 *
 * 2. Create an [OMGAPIAdmin] passing with [EWalletAdmin] as a parameter.
 *
 * For example,
 * <code>
 * val client = OMGAPIAdmin(eWalletAdmin)
 *
 * omgApiClient.getTransactions().enqueue(object : OMGCallback<PaginationList<Transaction>> {
 *      override fun success(response: OMGResponse<PaginationList<Transaction>>) {
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
class OMGAPIAdmin(internal val eWalletAdmin: EWalletAdmin) {
    private val eWalletAPI
        get() = eWalletAdmin.eWalletAPI

    /**
     * Asynchronously approve a transaction consumption
     * if *success* the `success` function will be invoked with the [OMGResponse<TransactionConsumption>] parameter,
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     */
    fun approveTransactionConsumption(params: TransactionConsumptionActionParams) = eWalletAPI.approveTransactionConsumption(params)

    /**
     * Asynchronously reject a transaction consumption
     * if *success* the `success` function will be invoked with the [OMGResponse<TransactionConsumption>] parameter,
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     */
    fun rejectTransactionConsumption(params: TransactionConsumptionActionParams) = eWalletAPI.rejectTransactionConsumption(params)

    /**
     * Asynchronously consume a transaction request
     * if *success* the `success` function will be invoked with the [OMGResponse<TransactionRequest>] parameter,
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     */
    fun consumeTransactionRequest(params: TransactionConsumptionParams) = eWalletAPI.consumeTransactionRequest(params)

    /**
     * Asynchronously send the request to send token to an address.
     * if *success* the `success` function will be invoked with the [OMGResponse<Transaction>] parameter,
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param params A set of parameters used for create a transaction
     */
    fun createTransaction(params: TransactionCreateParams) = eWalletAPI.createTransaction(params)

    /**
     * Asynchronously create a transaction request
     * if *success* the `success` function will be invoked with the [OMGResponse<TransactionRequest>] parameter,
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     */
    fun createTransactionRequest(params: TransactionRequestCreateParams) = eWalletAPI.createTransactionRequest(params)

    /**
     * Asynchronously send the request to get a wallet corresponding to an address.
     * if *success* the `success` function will be invoked with the [OMGResponse<Wallet>] parameter,
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param params A set of parameters used for getting a wallet.
     */
    fun getWallet(params: WalletParams) = eWalletAPI.getWallet(params)

    /**
     * Asynchronously send the request to get a transaction request corresponding to an id.
     * if *success* the `success` function will be invoked with the [OMGResponse<TransactionRequest>] parameter,
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param params A set of parameters used for getting a transaction request.
     */
    fun getTransactionRequest(params: TransactionRequestParams) = eWalletAPI.getTransactionRequest(params)

    /**
     * Asynchronously send the request to get a paginated transaction list.
     * if *success* the `success` function will be invoked with the [OMGResponse<PaginationList<Transaction>] parameter,
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param params A set of parameters used for get a paginated transaction list
     */
    fun getTransactions(params: TransactionListParams) = eWalletAPI.getTransactions(params)

    /**
     * Asynchronously send the request to get a paginated account list.
     * if *success* the `success` function will be invoked with the [OMGResponse<PaginationList<Account>>] parameter,
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param params A set of parameters used for get a paginated account list
     */
    fun getAccounts(params: AccountListParams) = eWalletAPI.getAccounts(params)

    /**
     * Asynchronously send the request to get a paginated token list.
     * if *success* the `success` function will be invoked with the [OMGResponse<PaginationList<Token>>] parameter,
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param params A set of parameters used for get a paginated token list
     */
    fun getTokens(params: TokenListParams) = eWalletAPI.getTokens(params)

    /**
     * Asynchronously send the request to get a paginated account's wallet list.
     * if *success* the `success` function will be invoked with the [OMGResponse<PaginationList<Wallet>>] parameter,
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param params A set of parameters used for get a paginated account's wallet list
     */
    fun getAccountWallets(params: AccountWalletListParams) = eWalletAPI.getAccountWallets(params)

    /**
     * Asynchronously send the request to get a paginated user's wallet list.
     * if *success* the `success` function will be invoked with the [OMGResponse<PaginationList<Wallet>>] parameter,
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param params A set of parameters used for get a paginated user's wallet list
     */
    fun getUserWallets(params: UserWalletListParams) = eWalletAPI.getUserWallets(params)

    /**
     * Asynchronously send the request to login.
     * if *success* the `success` function will be invoked with the [OMGResponse<AuthenticationToken>] parameter,
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     * @param params A set of parameters used for login
     */
    fun login(params: LoginParams) = eWalletAPI.login(params)

    /**
     * Asynchronously send the request to logout.
     * if *success* the `success` function will be invoked with the [OMGResponse<Logout>] parameter,
     * if *fail* the `fail` function will be invoked with the [OMGResponse<APIError>] parameter.
     *
     */
    fun logout() = eWalletAPI.logout()

    /**
     * Set new [authenticationToken].
     *
     * @param authenticationToken An authentication token to replace the old value.
     */
    fun setAuthenticationTokenHeader(authenticationToken: String) {
        eWalletAdmin.header.setHeader(authenticationToken)
    }
}
