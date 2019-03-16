package co.omisego.omisego.constant

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

object ClientAPIEndpoints {
    const val GET_CURRENT_USER = "me.get"
    const val LOGIN = "user.login"
    const val SIGN_UP = "user.signup"
    const val RESET_PASSWORD = "user.reset_password"
    const val UPDATE_PASSWORD = "user.update_password"
    const val LOGOUT = "me.logout"
    const val GET_WALLETS = "me.get_wallets"
    const val GET_TRANSACTIONS = "me.get_transactions"
    const val CREATE_TRANSACTION_REQUEST = "me.create_transaction_request"
    const val RETRIEVE_TRANSACTION_REQUEST = "me.get_transaction_request"
    const val CONSUME_TRANSACTION_REQUEST = "me.consume_transaction_request"
    const val APPROVE_TRANSACTION = "me.approve_transaction_consumption"
    const val REJECT_TRANSACTION = "me.reject_transaction_consumption"
    const val CREATE_TRANSACTION = "me.create_transaction"
    const val GET_SETTINGS = "me.get_settings"
}
