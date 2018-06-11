package co.omisego.omisego.constant.enums

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/7/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
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
    TOKEN_NOT_FOUND("token:token_not_found"),
    TRANSACTION_REQUEST_NOT_FOUND("transaction_request:transaction_request_not_found"),
    TRANSACTION_CONSUMPTION_NOT_OWNER("transaction_consumption:not_owner"),
    TRANSACTION_CONSUMPTION_INVALID_TOKEN("transaction_consumption:invalid_token"),
    TRANSACTION_CONSUMPTION_EXPIRED("transaction_consumption:expired"),
    TRANSACTION_CONSUMPTION_UNFINALIZED("transaction_consumption:unfinalized"),
    WEBSOCKET_FORBIDDEN_CHANNEL("websocket:forbidden_channel"),
    WEBSOCKET_CHANNEL_NOT_FOUND("websocket:channel_not_found"),

    //Error code from OmiseGO user API
    USER_ACCESS_TOKEN_EXPIRED("user:access_token_expired"),
    USER_ACCESS_TOKEN_NOT_FOUND("user:access_token_not_found"),
    USER_FROM_ADDRESS_NOT_FOUND("user:from_address_not_found"),
    USER_FROM_ADDRESS_MISMATCH("user:from_address_mismatch"),

    // Error code from OmiseGO SDK itself
    SDK_NETWORK_ERROR("sdk:network_error"),
    SDK_PARSE_ERROR("sdk:parse_error"),
    SDK_UNEXPECTED_ERROR("sdk:unexpected_error");

    override fun toString(): String = this.code

    companion object {
        /**
         * Convert an error code string to [ErrorCode]
         *
         * @param code An error code string
         * @return Enum error code representation.
         */
        fun from(code: String): ErrorCode {
            return try {
                ErrorCode.values().first { it.code == code }
            } catch (e: NoSuchElementException) {
                SDK_UNEXPECTED_ERROR
            }
        }
    }
}
