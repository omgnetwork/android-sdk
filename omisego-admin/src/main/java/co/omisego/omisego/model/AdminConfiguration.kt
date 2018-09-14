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
 * This class will be used for creating an instance of [EWalletAdmin].
 *
 * @param baseURL The base URL of the wallet server:
 * When initializing the [EWalletAdmin], this needs to be an http(s) url and need to be ended with '/'.
 *
 * For example,
 * for initialize [EWalletAdmin], the [baseURL] should end with '/api/admin/'
 *
 * @throws IllegalStateException if set with an empty string to [baseURL].
 */

data class AdminConfiguration internal constructor(
    override val baseURL: String,
    override var apiKey: String? = null,
    override var userId: String? = null,
    override val authenticationToken: String? = null
) : CredentialConfiguration {
    override val authScheme: AuthScheme = AuthScheme.ADMIN

    constructor(
        baseURL: String,
        userId: String? = null,
        authenticationToken: String? = null
    ) : this(baseURL, null, userId, authenticationToken)

    init {
        check(baseURL.isNotEmpty()) { Exceptions.MSG_EMPTY_BASE_URL }
    }
}
