package co.omisego.omisego

import co.omisego.omisego.custom.Callback
import co.omisego.omisego.custom.CallbackManager
import co.omisego.omisego.custom.Serializer
import co.omisego.omisego.model.Balance
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.Setting
import co.omisego.omisego.model.User
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
    fun getCurrentUser(callback: Callback<User>) =
            eWalletClient.eWalletAPI
                    .getCurrentUser()
                    .enqueue(
                            CallbackManager.newInstance<User>()
                                    .transform(callback)
                    )

    fun logout(callback: Callback<String>) =
            eWalletClient.eWalletAPI
                    .logout()
                    .enqueue(CallbackManager.newInstance<String>().transform(callback))

    fun listBalances(callback: Callback<List<Balance>>) =
            eWalletClient.eWalletAPI
                    .listBalance()
                    .enqueue(CallbackManager.newInstance<List<Balance>>().transform(callback))
}