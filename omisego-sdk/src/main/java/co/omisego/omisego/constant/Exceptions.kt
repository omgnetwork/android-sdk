package co.omisego.omisego.constant

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

object Exceptions {
    val emptyBaseURL
        get() = IllegalStateException("baseUrl should not be empty.")
    val emptyAuthenticationToken
        get() = IllegalStateException("Authentication token should not be empty.")

    const val MSG_EMPTY_BASE_URL = "baseUrl should not be empty."
    const val MSG_EMPTY_AUTH_TOKEN = "Authentication token should not be empty."
}