package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.util.Base64

object OMGEncryption {
    /**
     * Create an authentication header with Base64
     *
     * @param apiKey The apiKey can be retrieved from the Admin Panel.
     * @param authToken This can be retrieve by logging-in to the eWallet API.
     * @return An authorization header that ready to be used to connect to the eWallet API
     */
    fun createAuthorizationHeader(apiKey: String, authToken: String): String {
        return String(Base64.encode("$apiKey:$authToken".toByteArray(), Base64.NO_WRAP))
    }
}
