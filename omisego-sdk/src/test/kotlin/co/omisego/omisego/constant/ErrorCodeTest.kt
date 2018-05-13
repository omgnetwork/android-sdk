package co.omisego.omisego.constant

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 11/20/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.ErrorCode
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.junit.Test

class ErrorCodeTest {

    @Test
    fun `enum should be represent code correctly`() {
        val error = ErrorCode.CLIENT_INVALID_PARAMETER
        error.toString() shouldEqual "client:invalid_parameter"
        error shouldBeInstanceOf ErrorCode::class.java
    }

    @Test
    fun `enum code should be mapped correctly`() {
        val errorCodeList = ErrorCode.values().map { it.toString() }.toMutableList()
        errorCodeList.add("client:some_weird_error")
        for (code in errorCodeList) {
            val actual = ErrorCode.from(code)
            val expected = when (code) {
                "client:invalid_parameter" -> ErrorCode.CLIENT_INVALID_PARAMETER
                "client:invalid_version" -> ErrorCode.CLIENT_INVALID_VERSION
                "client:permission_error" -> ErrorCode.CLIENT_PERMISSION_ERROR
                "client:endpoint_not_found" -> ErrorCode.CLIENT_ENDPOINT_NOT_FOUND
                "client:invalid_api_key" -> ErrorCode.CLIENT_INVALID_API_KEY
                "client:invalid_auth_scheme" -> ErrorCode.CLIENT_INVALID_AUTH_SCHEME
                "server:internal_server_error" -> ErrorCode.SERVER_INTERNAL_SERVER_ERROR
                "server:unknown_error" -> ErrorCode.SERVER_UNKNOWN_ERROR
                "user:access_token_not_found" -> ErrorCode.USER_ACCESS_TOKEN_NOT_FOUND
                "user:access_token_expired" -> ErrorCode.USER_ACCESS_TOKEN_EXPIRED
                "user:from_address_not_found" -> ErrorCode.USER_FROM_ADDRESS_NOT_FOUND
                "user:from_address_mismatch" -> ErrorCode.USER_FROM_ADDRESS_MISMATCH
                "sdk:network_error" -> ErrorCode.SDK_NETWORK_ERROR
                "sdk:parse_error" -> ErrorCode.SDK_PARSE_ERROR
                "sdk:unknown_error" -> ErrorCode.SDK_UNEXPECTED_ERROR
                "token:token_not_found" -> ErrorCode.TOKEN_NOT_FOUND
                "sdk:socket_error" -> ErrorCode.SDK_SOCKET_ERROR
                "transaction_request:transaction_request_not_found" -> ErrorCode.TRANSACTION_REQUEST_NOT_FOUND
                "transaction_consumption:not_owner" -> ErrorCode.TRANSACTION_CONSUMPTION_NOT_OWNER
                "transaction_consumption:invalid_minted_token" -> ErrorCode.TRANSACTION_CONSUMPTION_INVALID_MINTED_TOKEN
                "transaction_consumption:expired" -> ErrorCode.TRANSACTION_CONSUMPTION_EXPIRED
                "transaction_consumption:unfinalized" -> ErrorCode.TRANSACTION_CONSUMPTION_UNFINALIZED
                "websocket:forbidden_channel" -> ErrorCode.WEBSOCKET_FORBIDDEN_CHANNEL
                "websocket:channel_not_found" -> ErrorCode.WEBSOCKET_CHANNEL_NOT_FOUND
                else -> ErrorCode.SDK_UNEXPECTED_ERROR
            }
            actual shouldEqual expected
        }
    }

    @Test
    fun `different error code should not equal`() {
        ErrorCode.SDK_NETWORK_ERROR shouldNotEqual ErrorCode.SDK_PARSE_ERROR
    }
}