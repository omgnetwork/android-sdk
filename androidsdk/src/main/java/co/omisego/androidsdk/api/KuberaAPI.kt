package co.omisego.androidsdk.api

import co.omisego.androidsdk.Callback
import co.omisego.androidsdk.models.Address
import co.omisego.androidsdk.models.Setting
import co.omisego.androidsdk.models.User


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/10/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

/**
 * An interface used to define all the available endpoints in the OmiseGO SDK.
 */
interface KuberaAPI {
    fun getCurrentUser(callback: Callback<User>)
    fun logout(callback: Callback<String>)
    fun listBalances(callback: Callback<List<Address>>)
    fun getSettings(callback: Callback<Setting>)
}