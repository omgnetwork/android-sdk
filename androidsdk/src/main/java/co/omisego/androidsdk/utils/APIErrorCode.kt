package co.omisego.androidsdk.utils


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/7/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

// Represents all error codes.
object APIErrorCode {
    val CLIENT_INVALID_PARAMETER = "client:invalid_parameter"
    val CLIENT_INVALID_VERSION = "client:invalid_version"
    val CLIENT_PERMISSION_ERROR = "client:permission_error"
    val CLIENT_ENDPOINT_NOT_FOUND = "client:endpoint_not_found"
    val CLIENT_INVALID_API_KEY = "client:invalid_api_key"
    val CLIENT_INVALID_AUTH_SCHEME = "client:invalid_auth_scheme"
    val SERVER_INTERNAL_SERVER_ERROR = "server:internal_server_error"
    val SERVER_UNKNOWN_ERROR = "server:unknown_error"
    val SERVER_ACCESS_TOKEN_NOT_FOUND = "server:access_token_not_found"
    val SERVER_ACCESS_TOKEN_EXPIRED = "server:access_token_expired"
}
