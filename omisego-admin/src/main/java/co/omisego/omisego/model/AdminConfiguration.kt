package co.omisego.omisego.model

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 23/5/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Exceptions
import co.omisego.omisego.constant.enums.AuthScheme

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

data class AdminConfiguration internal constructor(
    override val baseURL: String,
    override var apiKey: String? = null,
    override var userId: String? = null,
    override val authenticationToken: String? = null
) : CredentialConfiguration {
    override val authScheme: AuthScheme = AuthScheme.ADMIN

    /**
     * @param baseURL base url of the eWallet API.
     */
    constructor(
        baseURL: String
    ) : this(baseURL, null, null, null)

    init {
        check(baseURL.isNotEmpty()) { Exceptions.MSG_EMPTY_BASE_URL }
    }
}
