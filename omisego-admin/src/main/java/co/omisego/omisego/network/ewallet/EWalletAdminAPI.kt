package co.omisego.omisego.network.ewallet

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.AdminAPIEndpoints.ACCOUNT_ALL
import co.omisego.omisego.constant.AdminAPIEndpoints.LOGIN
import co.omisego.omisego.constant.AdminAPIEndpoints.LOGOUT
import co.omisego.omisego.constant.AdminAPIEndpoints.SWITCH_ACCOUNT
import co.omisego.omisego.constant.AdminAPIEndpoints.TRANSACTION_ALL
import co.omisego.omisego.constant.AdminAPIEndpoints.TRANSACTION_CALCULATE
import co.omisego.omisego.constant.AdminAPIEndpoints.TRANSACTION_CREATE
import co.omisego.omisego.custom.retrofit2.adapter.OMGCall
import co.omisego.omisego.model.Account
import co.omisego.omisego.model.AuthenticationToken
import co.omisego.omisego.model.Logout
import co.omisego.omisego.model.TransactionCalculation
import co.omisego.omisego.model.pagination.PaginationList
import co.omisego.omisego.model.params.AccountListParams
import co.omisego.omisego.model.params.LoginParams
import co.omisego.omisego.model.params.SwitchAccountParams
import co.omisego.omisego.model.params.TransactionCalculateParams
import co.omisego.omisego.model.transaction.Transaction
import co.omisego.omisego.model.transaction.list.TransactionListParams
import co.omisego.omisego.model.transaction.send.TransactionCreateParams
import retrofit2.http.Body
import retrofit2.http.POST

interface EWalletAdminAPI {
    @POST(LOGIN)
    fun login(@Body params: LoginParams): OMGCall<AuthenticationToken>

    @POST(TRANSACTION_CREATE)
    fun transfer(@Body params: TransactionCreateParams): OMGCall<Transaction>

    @POST(TRANSACTION_ALL)
    fun getTransactions(@Body params: TransactionListParams): OMGCall<PaginationList<Transaction>>

    @POST(ACCOUNT_ALL)
    fun getAccounts(@Body params: AccountListParams): OMGCall<PaginationList<Account>>

    @POST(SWITCH_ACCOUNT)
    fun switchAccount(@Body params: SwitchAccountParams): OMGCall<AuthenticationToken>

    @POST(TRANSACTION_CALCULATE)
    fun calculateTransaction(@Body params: TransactionCalculateParams): OMGCall<TransactionCalculation>

    @POST(LOGOUT)
    fun logout(): OMGCall<Logout>
}
