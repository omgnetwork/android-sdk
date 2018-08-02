package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 23/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Exceptions

/**
 * This class will be used for creating an instance of [OMGSocketClient] and [EWalletClient].
 *
 * @param baseURL The base URL of the wallet server:
 * When initializing the [EWalletClient], this needs to be an http(s) url
 * When initializing the [OMGSocketClient], this needs to be a ws(s) url
 * Both need to end with '/'
 *
 * For example,
 * for [OMGSocketClient], the [baseURL] should end with '/api/socket/'
 * for [EWalletClient], the [baseURL] should end with '/api/client/'
 *
 * @throws IllegalStateException if set with an empty string to [baseURL], [apiKey], or [authenticationToken].
 */

data class AdminConfiguration private constructor(
    override val baseURL: String,
    override val apiKey: String? = null,
    override val userId: String? = null,
    override val authenticationToken: String
) : CredentialConfiguration {
    override val authScheme: String = "OMGAdmin"

    /**
     * @param baseURL base url of the eWallet API.
     * @param userId A userId can be retrieved when logged in.
     * @param authenticationToken An authenticationToken is the token corresponding to an OmiseGO Wallet user retrievable using one of our server-side SDKs.
     */
    constructor(
        baseURL: String,
        userId: String,
        authenticationToken: String
    ) : this(baseURL, null, userId, authenticationToken)

    init {
        check(baseURL.isNotEmpty()) { Exceptions.MSG_EMPTY_BASE_URL }
        check(userId?.isNotEmpty() == true) { Exceptions.MSG_EMPTY_USER_ID }
        check(authenticationToken.isNotEmpty()) { Exceptions.MSG_EMPTY_AUTH_TOKEN }
    }
}
