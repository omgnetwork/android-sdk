package co.omisego.androidsdk.utils


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/7/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

// Represents all error codes.
enum class ErrorCode constructor(private val code: String) {

    //Error code from OmiseGO client API
    CLIENT_INVALID_PARAMETER("client:invalid_parameter"),
    CLIENT_INVALID_VERSION("client:invalid_version"),
    CLIENT_PERMISSION_ERROR("client:permission_error"),
    CLIENT_ENDPOINT_NOT_FOUND("client:endpoint_not_found"),
    CLIENT_INVALID_API_KEY("client:invalid_api_key"),
    CLIENT_INVALID_AUTH_SCHEME("client:invalid_auth_scheme"),

    // Error code from OmiseGO server API
    SERVER_INTERNAL_SERVER_ERROR("server:internal_server_error"),
    SERVER_UNKNOWN_ERROR("server:unknown_error"),

    //Error code from OmiseGO user API
    USER_ACCESS_TOKEN_EXPIRED("user:access_token_expired"),
    USER_ACCESS_TOKEN_NOT_FOUND("user:access_token_not_found"),

    // Error code from OmiseGO SDK itself
    SDK_NETWORK_ERROR("sdk:network_error"),
    SDK_PARSE_ERROR("sdk:parse_error"),
    SDK_UNKNOWN_ERROR("sdk:unknown_error");

    override fun toString(): String = this.code

    companion object {
        fun from(code: String): ErrorCode {
            return try {
                ErrorCode.values().first { it.code == code }
            } catch (e: NoSuchElementException) {
                SDK_UNKNOWN_ERROR
            }
        }
    }
}
