package co.omisego.omisego.custom.retrofit2.converter

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.exception.OMGAPIErrorException
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

internal class OMGConverterFactory(private val gson: Gson) : Converter.Factory() {

    companion object {
        fun create(gson: Gson): OMGConverterFactory {
            return OMGConverterFactory(gson)
        }
    }

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return OMGResponseConverter(gson, adapter)
    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<out Annotation>?, methodAnnotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return OMGRequestConverter(gson, adapter)
    }

    internal class OMGResponseConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : Converter<ResponseBody, T> {
        @Throws(IOException::class)
        override fun convert(responseBody: ResponseBody): T {
            try {
                /* Initialize JSONObject from the plain response string */
                val responseRaw = responseBody.string()

                /* Create response JSONObject to check the success flag */
                val response = JSONObject(responseRaw)

                /* Get the success flag */
                val success = response.getBoolean("success")

                return when {
                    success -> adapter.fromJson(responseRaw)
                    else -> {
                        val errorToken = object : TypeToken<OMGResponse<APIError>>() {}.type
                        val error = gson.fromJson<OMGResponse<APIError>>(responseRaw, errorToken)
                        throw OMGAPIErrorException(error)
                    }
                }
            } catch (e: JSONException) {
                throw IOException("Failed to parse JSON", e)
            }
        }
    }

    internal class OMGRequestConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : Converter<T, RequestBody> {
        override fun convert(value: T): RequestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(value))
    }
}
