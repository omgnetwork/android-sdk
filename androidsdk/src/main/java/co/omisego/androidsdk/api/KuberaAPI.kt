package co.omisego.androidsdk.api

import co.omisego.androidsdk.Callback
import co.omisego.androidsdk.models.Address
import co.omisego.androidsdk.models.Balance
import co.omisego.androidsdk.models.Setting
import co.omisego.androidsdk.models.User


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/10/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

interface KuberaAPI {
    val BASE_URL: String
        get() = "https://kubera.omisego.io/"

    fun getCurrentUser(callback: Callback<User>)
    fun logout()
    fun listBalances(callback: Callback<List<Address>>)
    fun getSettings(callback: Callback<Setting>)
}