package co.omisego.androidsdk

import co.omisego.androidsdk.api.KuberaAPI


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/10/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

object OMGApiClient : KuberaAPI {
    private var authorization: String = ""

    override fun getCurrentUser() {
        checkIfAuthorizationTokenSet(authorization)

    }

    override fun logout() {
        checkIfAuthorizationTokenSet(authorization)
    }

    override fun listBalances() {
        checkIfAuthorizationTokenSet(authorization)
    }

    override fun getSettings() {
        checkIfAuthorizationTokenSet(authorization)
    }

    private fun checkIfAuthorizationTokenSet(authorizationToken: String) {
        if (authorizationToken.isEmpty()) {
            throw IllegalStateException("OMGApiClient has not been initialized with the correct authorization token. Please call init(authorizationToken) first.")
        }
    }
}