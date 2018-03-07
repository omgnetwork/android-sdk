package co.omisego.omisego.utils

import co.omisego.omisego.constant.ErrorCode
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.custom.Callback
import co.omisego.omisego.custom.CallbackManager
import co.omisego.omisego.custom.Serializer
import co.omisego.omisego.model.ApiError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.Setting
import co.omisego.omisego.model.User
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.mock.Calls
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class CallbackManagerTest {
    private val failFile: File by lazy {
        File(javaClass.classLoader.getResource("fail.client-invalid_auth_scheme.json").path)
    }
    private val userFile: File by lazy {
        File(javaClass.classLoader.getResource("user.me-post.json").path)
    }

    private lateinit var responseObject: retrofit2.Callback<JsonElement>
    private lateinit var callbackUser: Callback<User>

    @Before
    fun setup() {
        callbackUser = mock()
        val type = object : TypeToken<OMGResponse<User>>() {}.type
        responseObject = CallbackManager<User>(Serializer(), type).transform(callbackUser)
    }

    @Test
    fun `Serializer should be parse successfully when the API return success false`() {
        val element = Gson().fromJson(failFile.readText(), JsonElement::class.java)
        responseObject.onResponse(Calls.response(element), Response.success(element))

        val apiError = ApiError(ErrorCode.CLIENT_INVALID_AUTH_SCHEME, "The provided authentication scheme is not supported")
        val expectedResult = OMGResponse(Versions.EWALLET_API, false, apiError)
        verify(callbackUser, times(1)).fail(expectedResult)
    }

    @Test
    fun `Serializer should be parse successfully when request timeout`() {
        responseObject.onFailure(Calls.failure(IOException("No internet connection")), SocketTimeoutException("Request timeout"))

        val apiError = ApiError(ErrorCode.SDK_NETWORK_ERROR, "Request timeout")
        val expectedResult = OMGResponse(Versions.EWALLET_API, false, apiError)
        verify(callbackUser, times(1)).fail(expectedResult)
    }

    @Test
    fun `Serializer should be parse successfully when the API return success true`() {
        val element = Gson().fromJson(userFile.readText(), JsonElement::class.java)
        responseObject.onResponse(Calls.response(element), Response.success(element))

        val user = User("48236187-9c5c-4568-8cdf-f0233d035574", "provider_user_id01", "user01", null)
        val expectedResult = OMGResponse(Versions.EWALLET_API, true, user)
        verify(callbackUser, times(1)).success(expectedResult)
    }
}