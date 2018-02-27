package co.omisego.omisego.exceptions

import javax.net.ssl.HttpsURLConnection


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/20/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

/**
 * Exception for an unexpected, 500 HTTP response.
 */
class OmiseGOServerException(private val errorCode: Int) : Exception() {
    override val message: String
        get() = when (errorCode) {
            HttpsURLConnection.HTTP_INTERNAL_ERROR -> "500 - Internal server error"
            HttpsURLConnection.HTTP_NOT_FOUND -> "404 - Endpoint not found"
            else -> "Unexpected error"
        }
}