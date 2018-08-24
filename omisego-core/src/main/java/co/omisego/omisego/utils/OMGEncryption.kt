package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Exceptions.MSG_EMPTY_API_KEY
import co.omisego.omisego.constant.Exceptions.MSG_EMPTY_AUTH_TOKEN
import co.omisego.omisego.constant.enums.AuthScheme.ADMIN
import co.omisego.omisego.constant.enums.AuthScheme.Client
import co.omisego.omisego.model.CredentialConfiguration

class OMGEncryption(
    private val encoder: Base64Encoder = Base64Encoder()
) {

    /**
     * Create an authentication header with Base64
     *
     * @param credentialConfiguration The configuration object that used for authenticate with the eWallet API.
     * @return An authorization header that ready to be used to connect to the eWallet API
     */
    fun createAuthorizationHeader(credentialConfiguration: CredentialConfiguration): String {
        return with(credentialConfiguration) {
            when (authScheme) {
                Client -> {
                    check(!authenticationToken.isNullOrEmpty()) { MSG_EMPTY_AUTH_TOKEN }
                    check(!apiKey.isNullOrEmpty()) { MSG_EMPTY_API_KEY }
                    encoder.encode(apiKey!!, authenticationToken!!)
                }
                ADMIN -> {
                    if (userId.isNullOrEmpty() || authenticationToken.isNullOrEmpty()) return ""
                    encoder.encode(userId!!, authenticationToken!!)
                }
            }
        }
    }
}
