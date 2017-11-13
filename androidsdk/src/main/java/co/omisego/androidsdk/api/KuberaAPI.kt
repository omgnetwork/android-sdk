package co.omisego.androidsdk.api


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/10/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

interface KuberaAPI {
    val BASE_URL: String
        get() = "https://kubera.omisego.io/"

    fun getCurrentUser()
    fun logout()
    fun listBalances()
    fun getSettings()
}