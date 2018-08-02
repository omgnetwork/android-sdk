package co.omisego.omisego.admin

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.admin.model.params.AccountListParams
import co.omisego.omisego.admin.model.params.LoginParams
import co.omisego.omisego.admin.model.params.SwitchAccountParams
import co.omisego.omisego.admin.model.params.TransactionCalculateParams
import co.omisego.omisego.admin.network.ewallet.EWalletAdmin
import co.omisego.omisego.model.transaction.list.TransactionListParams
import co.omisego.omisego.model.transaction.send.TransactionCreateParams

/**
 * The class OMGAPIClient represents an object that knows how to interact with OmiseGO API.
 *
 * To initialize [OMGAPIClient], the following steps should be taken.
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
 * 2. Create an [OMGAPIClient] passing with [EWalletAdmin] as a parameter.
 *
 * For example,
 * <code>
 * val client = OMGAPIClient(eWalletAdmin)
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
class OMGAPIClient(private val eWalletAdmin: EWalletAdmin) {
    private val eWalletAPI
        get() = eWalletAdmin.eWalletAPI

    fun login(params: LoginParams) = eWalletAPI.login(params)

    fun transfer(params: TransactionCreateParams) = eWalletAPI.transfer(params)

    fun getTransactions(params: TransactionListParams) = eWalletAPI.getTransactions(params)

    fun getAccounts(params: AccountListParams) = eWalletAPI.getAccounts(params)

    fun switchAccount(params: SwitchAccountParams) = eWalletAPI.switchAccount(params)

    fun calculateTransaction(params: TransactionCalculateParams) = eWalletAPI.calculateTransaction(params)

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
