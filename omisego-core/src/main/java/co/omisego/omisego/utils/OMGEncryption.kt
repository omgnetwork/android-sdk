package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.util.Base64
import co.omisego.omisego.constant.Exceptions.MSG_EMPTY_API_KEY_OR_USER_ID
import co.omisego.omisego.model.CredentialConfiguration

class OMGEncryption {

    /**
     * Create an authentication header with Base64
     *
     * @param credentialConfiguration The configuration object that used for authenticate with the eWallet API.
     * @return An authorization header that ready to be used to connect to the eWallet API
     */
    fun createAuthorizationHeader(credentialConfiguration: CredentialConfiguration): String {
        return with(credentialConfiguration) {
            when {
                !apiKey.isNullOrEmpty() -> String(Base64.encode("$apiKey:$authenticationToken".toByteArray(), Base64.NO_WRAP))
                !userId.isNullOrEmpty() -> String(Base64.encode("$userId:$authenticationToken".toByteArray(), Base64.NO_WRAP))
                else -> throw IllegalStateException(MSG_EMPTY_API_KEY_OR_USER_ID)
            }
        }
    }
}
