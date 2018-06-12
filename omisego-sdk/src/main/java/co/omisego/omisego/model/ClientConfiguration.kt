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
 * for [OMGSocketClient], the [baseURL] should be ended with '/api/socket/'
 * for [EWalletClient], the [baseURL] should be ended with '/api/'
 *
 * @param apiKey An apiKey is the API key (typically generated on the admin panel)
 * @param authenticationToken An authenticationToken is the token corresponding to an OmiseGO Wallet user retrievable using one of our server-side SDKs.
 * @throws IllegalStateException if set with an empty string to [baseURL], [apiKey], or [authenticationToken].
 */
data class ClientConfiguration(
    val baseURL: String,
    val apiKey: String,
    val authenticationToken: String
) {
    init {
        check(baseURL.isNotEmpty()) { Exceptions.MSG_EMPTY_BASE_URL }
        check(apiKey.isNotEmpty()) { Exceptions.MSG_EMPTY_API_KEY }
        check(authenticationToken.isNotEmpty()) { Exceptions.MSG_EMPTY_AUTH_TOKEN }
    }
}
