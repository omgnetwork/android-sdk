package co.omisego.omisego.constant

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 11/20/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

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
                "sdk:network_error" -> ErrorCode.SDK_NETWORK_ERROR
                "sdk:parse_error" -> ErrorCode.SDK_PARSE_ERROR
                "sdk:unknown_error" -> ErrorCode.SDK_UNKNOWN_ERROR
                else -> ErrorCode.SDK_UNKNOWN_ERROR
            }
            actual shouldEqual expected
        }
    }

    @Test
    fun `different error code should not equal`() {
        ErrorCode.SDK_NETWORK_ERROR shouldNotEqual ErrorCode.SDK_PARSE_ERROR
    }
}