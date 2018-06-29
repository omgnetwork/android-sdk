package co.omisego.omisego.custom.retrofit2.adapter

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 14/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.Versions
import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.exception.OMGAPIErrorException
import co.omisego.omisego.helpers.delegation.GsonDelegator
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.User
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.amshove.kluent.mock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.Executor

@RunWith(MockitoJUnitRunner::class)
class OMGCallbackWrapperTest : GsonDelegator() {
    private val userFile: File by ResourceFile("user.json")
    private val errorFile: File by ResourceFile("error-invalid_auth.json")
    private lateinit var sampleUser: User
    private lateinit var sampleError: APIError
    private lateinit var userResult: Response<OMGResponse<User>>
    private lateinit var mockCall: Call<OMGResponse<User>>
    private lateinit var mockOMGCallback: OMGCallback<User>
    private lateinit var callback: OMGCallbackWrapper<User>

    @Before
    fun setup() {
        mockCall = mock()
        mockOMGCallback = mock()
        val executor = Executor { it.run() }
        callback = OMGCallbackWrapper(mockOMGCallback, executor)
        sampleUser = gson.fromJson<OMGResponse<User>>(userFile.readText(),
                object : TypeToken<OMGResponse<User>>() {}.type).data
        sampleError = gson.fromJson<OMGResponse<APIError>>(errorFile.readText(),
                object : TypeToken<OMGResponse<APIError>>() {}.type).data
        userResult = Response.success(OMGResponse(Versions.EWALLET_API, true, sampleUser))
    }

    @Test
    fun `OMGCallbackWrapper delegates OMGAPIErrorException to the failed callback successfully`() {
        val expectedError = OMGResponse(Versions.EWALLET_API, false, sampleError)
        val throwable = OMGAPIErrorException(expectedError)

        callback.onFailure(mockCall, throwable)

        verify(mockOMGCallback, times(1)).fail(expectedError)
        verifyNoMoreInteractions(mockOMGCallback)
    }

    @Test
    fun `OMGCallbackWrapper delegates network error to the failed callback successfully`() {
        val errorMsg = "Request timeout."
        val throwable = IOException(errorMsg)
        val expectedError = OMGResponse(Versions.EWALLET_API, false, APIError(ErrorCode.SDK_NETWORK_ERROR, errorMsg))

        callback.onFailure(mockCall, throwable)

        verify(mockOMGCallback, times(1)).fail(expectedError)
        verifyNoMoreInteractions(mockOMGCallback)
    }

    @Test
    fun `OMGCallbackWrapper delegates the successful response to the success callback successfully`() {
        val expectedUser = OMGResponse(Versions.EWALLET_API, true, sampleUser)

        callback.onResponse(mockCall, userResult)

        verify(mockOMGCallback, times(1)).success(expectedUser)
        verifyNoMoreInteractions(mockOMGCallback)
    }

    @Test
    fun `OMGCallbackWrapper delegates the server 500 error to the failed callback successfully`() {
        val errorResponse = Response.error<OMGResponse<User>>(500, mock(ResponseBody::class))
        val expectedResponse = OMGResponse(Versions.EWALLET_API, false,
                APIError(ErrorCode.SERVER_INTERNAL_SERVER_ERROR,
                        "The EWallet API was 500 Internal Server Error"))

        callback.onResponse(mockCall, errorResponse)

        verify(mockOMGCallback, times(1)).fail(expectedResponse)
        verifyNoMoreInteractions(mockOMGCallback)
    }

    @Test
    fun `OMGCallbackWrapper delegates the unexpected error to the failed callback successfully`() {
        val response: Response<OMGResponse<User>> = Response.success(null)

        val expectedResponse = OMGResponse(Versions.EWALLET_API, false,
                APIError(ErrorCode.SDK_UNEXPECTED_ERROR,
                        "Unexpected Error"))

        callback.onResponse(mockCall, response)

        verify(mockOMGCallback, times(1)).fail(expectedResponse)
        verifyNoMoreInteractions(mockOMGCallback)
    }

    @Test
    fun `OMGCallbackWrapper delegates the server unknown error to the failed callback successfully`() {
        val json = MediaType.parse("application/json")
        val responseText = "A bug is never just a mistake. It represents something bigger."
        val body = ResponseBody.create(json, responseText)
        val response: Response<OMGResponse<User>> = Response.error(400, body)

        val expectedResponse = OMGResponse(Versions.EWALLET_API, false,
                APIError(ErrorCode.SERVER_UNKNOWN_ERROR, responseText))

        callback.onResponse(mockCall, response)

        verify(mockOMGCallback, times(1)).fail(expectedResponse)
        verifyNoMoreInteractions(mockOMGCallback)
    }
}
