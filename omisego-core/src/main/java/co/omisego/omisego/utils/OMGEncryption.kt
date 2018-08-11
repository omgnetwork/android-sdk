package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.util.Base64
import co.omisego.omisego.constant.Exceptions.MSG_EMPTY_API_KEY
import co.omisego.omisego.constant.Exceptions.MSG_EMPTY_AUTH_TOKEN
import co.omisego.omisego.constant.enums.AuthScheme.ADMIN
import co.omisego.omisego.constant.enums.AuthScheme.Client
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
            if (authenticationToken.isNullOrEmpty()) throw IllegalStateException(MSG_EMPTY_AUTH_TOKEN)
            when (authScheme) {
                Client -> {
                    if (apiKey.isNullOrEmpty()) throw IllegalStateException(MSG_EMPTY_API_KEY)
                    String(Base64.encode("$apiKey:$authenticationToken".toByteArray(), Base64.NO_WRAP))
                }
                ADMIN -> {
                    if (userId.isNullOrEmpty()) return ""
                    String(Base64.encode("$userId:$authenticationToken".toByteArray(), Base64.NO_WRAP))
                }
            }
        }
    }
}
