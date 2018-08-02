package co.omisego.omisego.admin.network.ewallet

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.admin.constant.Endpoints.ACCOUNT_ALL
import co.omisego.omisego.admin.constant.Endpoints.LOGIN
import co.omisego.omisego.admin.constant.Endpoints.LOGOUT
import co.omisego.omisego.admin.constant.Endpoints.SWITCH_ACCOUNT
import co.omisego.omisego.admin.constant.Endpoints.TRANSACTION_ALL
import co.omisego.omisego.admin.constant.Endpoints.TRANSACTION_CALCULATE
import co.omisego.omisego.admin.constant.Endpoints.TRANSACTION_CREATE
import co.omisego.omisego.admin.model.AuthenticationToken
import co.omisego.omisego.admin.model.TransactionCalculation
import co.omisego.omisego.admin.model.params.AccountListParams
import co.omisego.omisego.admin.model.params.LoginParams
import co.omisego.omisego.admin.model.params.SwitchAccountParams
import co.omisego.omisego.admin.model.params.TransactionCalculateParams
import co.omisego.omisego.custom.retrofit2.adapter.OMGCall
import co.omisego.omisego.model.Account
import co.omisego.omisego.model.Logout
import co.omisego.omisego.model.pagination.PaginationList
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
