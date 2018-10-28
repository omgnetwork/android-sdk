package co.omisego.omisego.live

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 18/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.Account
import co.omisego.omisego.model.AdminAuthenticationToken
import co.omisego.omisego.model.TransactionRequest
import co.omisego.omisego.model.Wallet
import co.omisego.omisego.model.params.AccountListParams
import co.omisego.omisego.model.params.AccountWalletListParams
import co.omisego.omisego.model.params.LoginParams
import co.omisego.omisego.model.params.TransactionRequestParams
import co.omisego.omisego.model.params.admin.TransactionRequestCreateParams
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
open class BaseAuthTest : BaseLiveTest() {

    lateinit var testAdminAuthenticationToken: AdminAuthenticationToken

    private val testAccountList: List<Account> by lazy {
        getAccount()
    }

    /* Token for testing */
    val testTokenId by lazy { secret.getString("token_id") }

    /*  Accounts for testing */
    val testMasterAccount: Account by lazy { testAccountList.find { it.master }!! }
    val testBrandAccount: Account by lazy { testAccountList.find { !it.master }!! }

    /* Wallets for testing*/
    val testMasterWallet: Wallet by lazy { testMasterAccount.getWallet() }
    val testBrandWallet: Wallet by lazy { testBrandAccount.getWallet() }

    @Before
    open fun setup() {
        val response = client.login(
            LoginParams(
                secret.getString("email"),
                secret.getString("password")
            )
        ).execute()
        response.isSuccessful shouldBe true
        testAdminAuthenticationToken = response.body()!!.data
    }

    @Test
    fun `should be setup authentication correctly`() {
        testAdminAuthenticationToken.authenticationToken.isEmpty() shouldBe false
        testAdminAuthenticationToken.accountId.isEmpty() shouldBe false
        testAdminAuthenticationToken.account shouldNotBe null
    }

    fun createTransactionRequest(params: TransactionRequestCreateParams): TransactionRequest {
        val body = client.createTransactionRequest(params).execute().body()
        return body?.data!!
    }

    private fun getAccount(params: AccountListParams = AccountListParams()): List<Account> {
        val accountList = client.getAccounts(params).execute()
        return accountList.body()?.data?.data!!
    }

    fun Account.getWallet(): Wallet {
        return client.getAccountWallets(params = AccountWalletListParams(id = this.id)).execute().body()?.data?.data?.get(1)!!
    }
}
