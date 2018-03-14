package co.omisego.omisego.custom.retrofit2.adapter

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 14/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.ErrorCode
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.custom.gson.ErrorCodeDeserializer
import co.omisego.omisego.exception.OMGAPIErrorException
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.User
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import okhttp3.ResponseBody
import org.amshove.kluent.mock
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.IOException

class OMGCallbackWrapperTest {
    private val userFile: File by ResourceFile("user.me-post.json")
    private val errorFile: File by ResourceFile("fail.client-invalid_auth_scheme.json")
    private lateinit var sampleUser: User
    private lateinit var sampleError: APIError
    private lateinit var userResult: Response<OMGResponse<User>>
    private lateinit var mockCall: Call<OMGResponse<User>>
    private lateinit var mockOMGCallback: OMGCallback<User>
    private lateinit var callback: OMGCallbackWrapper<User>
    private val gson by lazy {
        GsonBuilder()
                .registerTypeAdapter(ErrorCode::class.java, ErrorCodeDeserializer())
                .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
    }

    @Before
    fun setup() {
        mockCall = mock()
        mockOMGCallback = mock()
        callback = OMGCallbackWrapper(mockOMGCallback)
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
    }

    @Test
    fun `OMGCallbackWrapper delegates network error to the failed callback successfully`() {
        val errorMsg = "Request timeout."
        val throwable = IOException(errorMsg)
        val expectedError = OMGResponse(Versions.EWALLET_API, false, APIError(ErrorCode.SDK_NETWORK_ERROR, errorMsg))

        callback.onFailure(mockCall, throwable)

        verify(mockOMGCallback, times(1)).fail(expectedError)
    }

    @Test
    fun `OMGCallbackWrapper delegates the successful response to the success callback successfully`() {
        val expectedUser = OMGResponse(Versions.EWALLET_API, true, sampleUser)

        callback.onResponse(mockCall, userResult)

        verify(mockOMGCallback, times(1)).success(expectedUser)
    }


    @Test
    fun `OMGCallbackWrapper delegates the server 500 error to the failed callback successfully`() {
        val errorResponse = Response.error<OMGResponse<User>>(500, mock(ResponseBody::class))
        val expectedResponse = OMGResponse(Versions.EWALLET_API, false,
                APIError(ErrorCode.SERVER_INTERNAL_SERVER_ERROR,
                        "The EWallet API was 500 Internal Server Error"))

        callback.onResponse(mockCall, errorResponse)

        verify(mockOMGCallback, times(1)).fail(expectedResponse)
    }

    @Test
    fun `OMGCallbackWrapper delegates the parsed error to the failed callback successfully`() {
        val mockResponse = mock<Response<OMGResponse<User>>>()
        whenever(mockResponse.body()).thenReturn(null)

        val expectedResponse = OMGResponse(Versions.EWALLET_API, false,
                APIError(ErrorCode.SDK_PARSE_ERROR,
                        "The response body was null"))

        callback.onResponse(mockCall, mockResponse)

        verify(mockOMGCallback, times(1)).fail(expectedResponse)
    }

    @Test
    fun `OMGCallbackWrapper delegates the unexpected error to the failed callback successfully`() {
        val mockResponse = mock<Response<OMGResponse<User>>>()
        val omgResponse = OMGResponse(Versions.EWALLET_API, false, User("1234", "1234", "1234", mapOf()))
        whenever(mockResponse.body()).thenReturn(omgResponse)
        whenever(mockResponse.isSuccessful).thenReturn(false)

        val expectedResponse = OMGResponse(Versions.EWALLET_API, false,
                APIError(ErrorCode.SDK_UNEXPECTED_ERROR,
                        "Unexpected Error"))

        callback.onResponse(mockCall, mockResponse)

        verify(mockOMGCallback, times(1)).fail(expectedResponse)
    }
}
