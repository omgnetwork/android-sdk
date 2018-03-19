package co.omisego.omisego.custom.retrofit2.converter

/*
 * OmiseGO
 *
 *
 * Created by Phuchit Sirimongkolsathien on 14/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.ErrorCode
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.custom.gson.ErrorCodeDeserializer
import co.omisego.omisego.exception.OMGAPIErrorException
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.User
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.io.IOException

class OMGConverterFactoryTest {
    @Rule
    @JvmField
    val expectedEx = ExpectedException.none()!!
    private val userFile: File by ResourceFile("user.me-post.json")
    private val errorFile: File by ResourceFile("fail.client-invalid_auth_scheme.json")
    private lateinit var gson: Gson
    private lateinit var responseBody: ResponseBody
    private lateinit var omgConverterFactory: OMGConverterFactory
    private lateinit var omgConverter: Converter<ResponseBody, *>
    private lateinit var sampleError: APIError
    private lateinit var sampleUser: User
    private lateinit var userResult: Response<OMGResponse<User>>

    @Before
    fun setup() {
        gson = GsonBuilder()
                .registerTypeAdapter(ErrorCode::class.java, ErrorCodeDeserializer())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
        val userType = object : TypeToken<OMGResponse<User>>() {}.type
        val errorType = object : TypeToken<OMGResponse<APIError>>() {}.type
        val retrofit = Retrofit.Builder().baseUrl("http://localhost:8080/").build()
        omgConverterFactory = OMGConverterFactory.create(gson)
        omgConverter = omgConverterFactory.responseBodyConverter(userType, arrayOf(), retrofit)
        sampleError = gson.fromJson<OMGResponse<APIError>>(errorFile.readText(), errorType).data
        sampleUser = gson.fromJson<OMGResponse<User>>(userFile.readText(), userType).data
        userResult = Response.success(OMGResponse(Versions.EWALLET_API, true, sampleUser))
    }

    @Test
    fun `OMGConverterFactory should parse the successful response successfully`() {
        responseBody = ResponseBody.create(MediaType.parse("application/json"), userFile.readText())

        val actualResponse = omgConverter.convert(responseBody)
        val expectedResponse = OMGResponse(Versions.EWALLET_API, true, sampleUser)

        actualResponse shouldEqual expectedResponse
    }

    @Test
    fun `OMGConverterFactory should parse the failed response successfully`() {
        responseBody = ResponseBody.create(MediaType.parse("application/json"), errorFile.readText())

        val expectedResponse = OMGResponse(Versions.EWALLET_API, false, sampleError)
        expectedEx.expect(OMGAPIErrorException::class.java)
        expectedEx.expectMessage(expectedResponse.toString())

        omgConverter.convert(responseBody)
    }

    @Test
    fun `OMGConverterFactory should throw IOException when receives illegal json format`() {
        responseBody = ResponseBody.create(MediaType.parse("application/json"), "Bonjour le monde?")

        expectedEx.expect(IOException::class.java)
        expectedEx.expectMessage("Failed to parse JSON")

        omgConverter.convert(responseBody)
    }
}
