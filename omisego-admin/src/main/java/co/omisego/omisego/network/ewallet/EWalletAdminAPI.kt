package co.omisego.omisego.network.ewallet

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.AdminAPIEndpoints.ACCOUNT_ALL
import co.omisego.omisego.constant.AdminAPIEndpoints.ACCOUNT_GET_WALLETS
import co.omisego.omisego.constant.AdminAPIEndpoints.LOGIN
import co.omisego.omisego.constant.AdminAPIEndpoints.LOGOUT
import co.omisego.omisego.constant.AdminAPIEndpoints.TOKEN_ALL
import co.omisego.omisego.constant.AdminAPIEndpoints.TRANSACTION_ALL
import co.omisego.omisego.constant.AdminAPIEndpoints.TRANSACTION_CREATE
import co.omisego.omisego.constant.AdminAPIEndpoints.TRANSACTION_REQUEST_APPROVE
import co.omisego.omisego.constant.AdminAPIEndpoints.TRANSACTION_REQUEST_CONSUME
import co.omisego.omisego.constant.AdminAPIEndpoints.TRANSACTION_REQUEST_CREATE
import co.omisego.omisego.constant.AdminAPIEndpoints.TRANSACTION_REQUEST_GET
import co.omisego.omisego.constant.AdminAPIEndpoints.TRANSACTION_REQUEST_REJECT
import co.omisego.omisego.constant.AdminAPIEndpoints.USER_GET_WALLETS
import co.omisego.omisego.constant.AdminAPIEndpoints.WALLET_GET
import co.omisego.omisego.custom.retrofit2.adapter.OMGCall
import co.omisego.omisego.model.Account
import co.omisego.omisego.model.AdminAuthenticationToken
import co.omisego.omisego.model.Empty
import co.omisego.omisego.model.Token
import co.omisego.omisego.model.Transaction
import co.omisego.omisego.model.TransactionConsumption
import co.omisego.omisego.model.TransactionRequest
import co.omisego.omisego.model.Wallet
import co.omisego.omisego.model.pagination.PaginationList
import co.omisego.omisego.model.params.AccountListParams
import co.omisego.omisego.model.params.AccountWalletListParams
import co.omisego.omisego.model.params.LoginParams
import co.omisego.omisego.model.params.TokenListParams
import co.omisego.omisego.model.params.TransactionConsumptionActionParams
import co.omisego.omisego.model.params.TransactionListParams
import co.omisego.omisego.model.params.TransactionRequestParams
import co.omisego.omisego.model.params.UserWalletListParams
import co.omisego.omisego.model.params.WalletParams
import co.omisego.omisego.model.params.admin.TransactionConsumptionParams
import co.omisego.omisego.model.params.admin.TransactionCreateParams
import co.omisego.omisego.model.params.admin.TransactionRequestCreateParams
import retrofit2.http.Body
import retrofit2.http.POST

interface EWalletAdminAPI {
    @POST(TRANSACTION_REQUEST_APPROVE)
    fun approveTransactionConsumption(@Body params: TransactionConsumptionActionParams): OMGCall<TransactionConsumption>

    @POST(TRANSACTION_REQUEST_REJECT)
    fun rejectTransactionConsumption(@Body params: TransactionConsumptionActionParams): OMGCall<TransactionConsumption>

    @POST(TRANSACTION_REQUEST_CONSUME)
    fun consumeTransactionRequest(
        @Body params: TransactionConsumptionParams
    ): OMGCall<TransactionConsumption>

    @POST(TRANSACTION_CREATE)
    fun createTransaction(@Body params: TransactionCreateParams): OMGCall<Transaction>

    @POST(TRANSACTION_REQUEST_CREATE)
    fun createTransactionRequest(@Body params: TransactionRequestCreateParams): OMGCall<TransactionRequest>

    @POST(TRANSACTION_ALL)
    fun getTransactions(@Body params: TransactionListParams): OMGCall<PaginationList<Transaction>>

    @POST(TRANSACTION_REQUEST_GET)
    fun getTransactionRequest(@Body params: TransactionRequestParams): OMGCall<TransactionRequest>

    @POST(ACCOUNT_ALL)
    fun getAccounts(@Body params: AccountListParams): OMGCall<PaginationList<Account>>

    @POST(ACCOUNT_GET_WALLETS)
    fun getAccountWallets(@Body params: AccountWalletListParams): OMGCall<PaginationList<Wallet>>

    @POST(WALLET_GET)
    fun getWallet(@Body params: WalletParams): OMGCall<Wallet>

    @POST(USER_GET_WALLETS)
    fun getUserWallets(@Body params: UserWalletListParams): OMGCall<PaginationList<Wallet>>

    @POST(TOKEN_ALL)
    fun getTokens(@Body params: TokenListParams): OMGCall<PaginationList<Token>>

    @POST(LOGIN)
    fun login(@Body params: LoginParams): OMGCall<AdminAuthenticationToken>

    @POST(LOGOUT)
    fun logout(): OMGCall<Empty>
}
