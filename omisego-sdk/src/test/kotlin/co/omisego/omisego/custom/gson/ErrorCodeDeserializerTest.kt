package co.omisego.omisego.custom.gson

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 13/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.helpers.delegation.GsonDelegator
import org.amshove.kluent.shouldEqual
import org.junit.Test

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 13/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class ErrorCodeDeserializerTest : GsonDelegator() {
    data class TestData(
        val code: ErrorCode
    )

    @Test
    fun `ErrorCode should be deserialized successfully`() {
        val errorResponse = """{"code": "transaction_request:transaction_request_not_found"}"""

        val response = gson.fromJson(errorResponse, TestData::class.java)

        response.code shouldEqual ErrorCode.TRANSACTION_REQUEST_NOT_FOUND
    }
}
