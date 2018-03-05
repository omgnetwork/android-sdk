package co.omisego.omisego

import co.omisego.omisego.models.Balance
import co.omisego.omisego.models.Setting
import co.omisego.omisego.models.User
import co.omisego.omisego.networks.core.ewallet.EWalletClient
import co.omisego.omisego.utils.ResponseSerializer


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class NewOMGAPIClient(private val eWalletClient: EWalletClient) {
    fun getCurrentUser(callback: Callback<User>) {
        val responseSerializer = ResponseSerializer<User>()
        eWalletClient.eWalletAPI.getCurrentUser().enqueue(responseSerializer.default(callback))
    }

    fun getSetting(callback: Callback<Setting>) {
        val responseSerializer = ResponseSerializer<Setting>()
        eWalletClient.eWalletAPI.getSettings().enqueue(responseSerializer.default(callback))
    }

    fun logout(callback: Callback<String>) {
        val responseSerializer = ResponseSerializer<String>()
        eWalletClient.eWalletAPI.logout().enqueue(responseSerializer.default(callback))
    }

    fun listBalances(callback: Callback<List<Balance>>) {
        val responseSerializer = ResponseSerializer<List<Balance>>()
        eWalletClient.eWalletAPI.listBalance().enqueue(responseSerializer.default(callback))
    }
}