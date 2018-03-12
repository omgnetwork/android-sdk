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
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Type


internal class OMGConverterFactory(private val gson: Gson) : Converter.Factory() {

    companion object {
        fun create(gson: Gson): OMGConverterFactory {
            return OMGConverterFactory(gson)
        }
    }

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return OMGConverter(gson, adapter)
    }

    internal class OMGConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : Converter<ResponseBody, OMGResponse<T>> {
        @Throws(IOException::class)
        override fun convert(responseBody: ResponseBody): OMGResponse<T> {
            try {
                /* Initialize JSONObject from plain response string */
                val response = JSONObject(responseBody.string())

                /* Get the data object */
                val data = response.getJSONObject("data")

                /* Get the success flag */
                val success = response.getBoolean("success")

                /* Get the version */
                val version = response.getString("version")

                /* Initialize reader for gson */
                val reader = InputStreamReader(ByteArrayInputStream(data.toString().toByteArray()))

                when (success) {
                    true -> {
                        val model = adapter.read(gson.newJsonReader(reader))
                        return OMGResponse(version, success, model)
                    }
                    false -> {
                        val error = gson.fromJson<APIError>(reader, APIError::class.java)
                        throw OMGAPIErrorException(OMGResponse(version, success, error))
                    }
                }
            } catch (e: JSONException) {
                throw IOException("Failed to parse JSON", e)
            }
        }
    }
}