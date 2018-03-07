package co.omisego.omisego.custom

/**
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 7/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.ErrorCode
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.model.ApiError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.User
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import org.amshove.kluent.shouldEqual
import retrofit2.Response
import java.io.File
import java.io.IOException
import kotlin.test.Test

class SerializerTest {
    private val serializer by lazy { Serializer() }
    private val failFile: File by lazy {
        File(javaClass.classLoader.getResource("fail.client-invalid_auth_scheme.json").path)
    }
    private val userFile: File by lazy {
        File(javaClass.classLoader.getResource("user.me-post.json").path)
    }

    @Test
    fun `Serialize throwable failure`() {
        val msg = "No internet connection"
        val actual = serializer.failure(IOException(msg))
        val expected = OMGResponse(Versions.EWALLET_API, false, ApiError(ErrorCode.SDK_NETWORK_ERROR, msg))
        actual shouldEqual expected
    }

    @Test
    fun `Serialize API failure`() {
        val element = Gson().fromJson(failFile.readText(), JsonElement::class.java)
        val response = Response.success(element)
        val actual = serializer.failure(response)
        val expected = OMGResponse(Versions.EWALLET_API, false,
                ApiError(ErrorCode.CLIENT_INVALID_AUTH_SCHEME, "The provided authentication scheme is not supported"))
        actual shouldEqual expected
    }

    @Test
    fun `Serialize API success`() {
        val element = Gson().fromJson(userFile.readText(), JsonElement::class.java)
        val response = Response.success(element)
        val user = User("48236187-9c5c-4568-8cdf-f0233d035574", "provider_user_id01", "user01", null)
        val actual = serializer.success<OMGResponse<User>>(response, object: TypeToken<OMGResponse<User>>() {}.type)
        val expected = OMGResponse(Versions.EWALLET_API, true, user)
        actual shouldEqual expected
    }
}