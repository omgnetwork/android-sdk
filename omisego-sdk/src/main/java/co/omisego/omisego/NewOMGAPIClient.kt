package co.omisego.omisego

import co.omisego.omisego.custom.Callback
import co.omisego.omisego.custom.CallbackManager
import co.omisego.omisego.custom.Serializer
import co.omisego.omisego.model.*
import co.omisego.omisego.network.ewallet.EWalletClient
import com.google.gson.reflect.TypeToken


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class NewOMGAPIClient(private val eWalletClient: EWalletClient) {
    private val serializer = Serializer()
    fun getCurrentUser(callback: Callback<User>) {
        val type = object : TypeToken<OMGResponse<User>>() {}.type
        val callbackManager = CallbackManager<User>(serializer, type)
        eWalletClient.eWalletAPI.getCurrentUser().enqueue(callbackManager.transform(callback))
    }

    fun getSettings(callback: Callback<Setting>) {
        val type = object : TypeToken<OMGResponse<Setting>>() {}.type
        val callbackManager = CallbackManager<Setting>(serializer, type)
        eWalletClient.eWalletAPI.getSettings().enqueue(callbackManager.transform(callback))
    }

    fun logout(callback: Callback<Logout>) {
        val type = object : TypeToken<OMGResponse<Logout>>() {}.type
        val callbackManager = CallbackManager<Logout>(serializer, type)
        eWalletClient.eWalletAPI.logout().enqueue(callbackManager.transform(callback))
    }

    fun listBalances(callback: Callback<BalanceList>) {
        val type = object : TypeToken<OMGResponse<BalanceList>>() {}.type
        val callbackManager = CallbackManager<BalanceList>(serializer, type)
        eWalletClient.eWalletAPI.listBalance().enqueue(callbackManager.transform(callback))
    }

}