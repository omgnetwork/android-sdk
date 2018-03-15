package co.omisego.omisego.custom.gson

import co.omisego.omisego.constant.ErrorCode
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.amshove.kluent.shouldEqual
import org.junit.Test

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 13/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */


class ErrorCodeDeserializerTest {

    private val errorFile by ResourceFile("fail.client-invalid_auth_scheme.json")

    @Test
    fun `ErrorCode should be deserialized successfully`() {
        val gson = GsonBuilder()
                .registerTypeAdapter(ErrorCode::class.java, ErrorCodeDeserializer())
                .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

        val typeToken = object : TypeToken<OMGResponse<APIError>>() {}.type
        val response = gson.fromJson<OMGResponse<APIError>>(errorFile.readText(), typeToken)

        val expected = OMGResponse(Versions.EWALLET_API, false,
                APIError(
                        ErrorCode.CLIENT_INVALID_AUTH_SCHEME,
                        "The provided authentication scheme is not supported"
                )
        )

        response shouldEqual expected
    }
}
