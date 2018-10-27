package co.omisego.omisego.network.ewallet

import co.omisego.omisego.constant.ClientAPIEndpoints.APPROVE_TRANSACTION
import co.omisego.omisego.constant.ClientAPIEndpoints.CONSUME_TRANSACTION_REQUEST
import co.omisego.omisego.constant.ClientAPIEndpoints.CREATE_TRANSACTION_REQUEST
import co.omisego.omisego.constant.ClientAPIEndpoints.GET_CURRENT_USER
import co.omisego.omisego.constant.ClientAPIEndpoints.GET_SETTINGS
import co.omisego.omisego.constant.ClientAPIEndpoints.GET_TRANSACTIONS
import co.omisego.omisego.constant.ClientAPIEndpoints.GET_WALLETS
import co.omisego.omisego.constant.ClientAPIEndpoints.LOGIN
import co.omisego.omisego.constant.ClientAPIEndpoints.LOGOUT
import co.omisego.omisego.constant.ClientAPIEndpoints.REJECT_TRANSACTION
import co.omisego.omisego.constant.ClientAPIEndpoints.RETRIEVE_TRANSACTION_REQUEST
import co.omisego.omisego.constant.ClientAPIEndpoints.SIGN_UP
import co.omisego.omisego.constant.ClientAPIEndpoints.TRANSFER
import co.omisego.omisego.custom.retrofit2.adapter.OMGCall
import co.omisego.omisego.model.ClientAuthenticationToken
import co.omisego.omisego.model.Empty
import co.omisego.omisego.model.Setting
import co.omisego.omisego.model.Transaction
import co.omisego.omisego.model.TransactionConsumption
import co.omisego.omisego.model.TransactionRequest
import co.omisego.omisego.model.User
import co.omisego.omisego.model.WalletList
import co.omisego.omisego.model.pagination.PaginationList
import co.omisego.omisego.model.params.LoginParams
import co.omisego.omisego.model.params.SignUpParams
import co.omisego.omisego.model.params.TransactionConsumptionActionParams
import co.omisego.omisego.model.params.TransactionCreateParams
import co.omisego.omisego.model.params.TransactionListParams
import co.omisego.omisego.model.params.TransactionRequestParams
import co.omisego.omisego.model.params.client.TransactionConsumptionParams
import co.omisego.omisego.model.params.client.TransactionRequestCreateParams
import retrofit2.http.Body
import retrofit2.http.POST

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

interface EWalletClientAPI {
    @POST(GET_CURRENT_USER)
    fun getCurrentUser(): OMGCall<User>

    @POST(LOGIN)
    fun login(@Body params: LoginParams): OMGCall<ClientAuthenticationToken>

    @POST(SIGN_UP)
    fun signup(@Body params: SignUpParams): OMGCall<Empty>

    @POST(LOGOUT)
    fun logout(): OMGCall<Empty>

    @POST(GET_WALLETS)
    fun getWallets(): OMGCall<WalletList>

    @POST(GET_SETTINGS)
    fun getSettings(): OMGCall<Setting>

    @POST(GET_TRANSACTIONS)
    fun getTransactions(@Body request: TransactionListParams): OMGCall<PaginationList<Transaction>>

    @POST(CREATE_TRANSACTION_REQUEST)
    fun createTransactionRequest(@Body request: TransactionRequestCreateParams): OMGCall<TransactionRequest>

    @POST(RETRIEVE_TRANSACTION_REQUEST)
    fun retrieveTransactionRequest(@Body request: TransactionRequestParams): OMGCall<TransactionRequest>

    @POST(CONSUME_TRANSACTION_REQUEST)
    fun consumeTransactionRequest(
        @Body request: TransactionConsumptionParams
    ): OMGCall<TransactionConsumption>

    @POST(APPROVE_TRANSACTION)
    fun approveTransactionConsumption(
        @Body request: TransactionConsumptionActionParams
    ): OMGCall<TransactionConsumption>

    @POST(REJECT_TRANSACTION)
    fun rejectTransactionConsumption(
        @Body request: TransactionConsumptionActionParams
    ): OMGCall<TransactionConsumption>

    @POST(TRANSFER)
    fun transfer(
        @Body request: TransactionCreateParams
    ): OMGCall<Transaction>
}
