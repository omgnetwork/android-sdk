package co.omisego.omisego.custom.retrofit2.converter

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.ErrorCode
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.exception.OMGAPIErrorException
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Type


internal class OMGConverterFactory(private val gson: Gson) : Converter.Factory() {

    companion object {
        fun create(gson: Gson = Gson()): OMGConverterFactory {
            return OMGConverterFactory(gson)
        }
    }

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return OMGConverter(gson, adapter)
    }

    internal class OMGConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : Converter<ResponseBody, T> {
        @Throws(IOException::class)
        override fun convert(responseBody: ResponseBody): T {
            try {
                val responseString = responseBody.string()
                val reader = InputStreamReader(ByteArrayInputStream(responseString.toByteArray()))
                val rawResponse = gson.fromJson(responseString, JsonElement::class.java)
                val success = rawResponse.asJsonObject.get("success").asBoolean
                if (success) {
                    /* Parse success response */
                    val jsonReader = gson.newJsonReader(reader)
                    return adapter.read(jsonReader)
                } else {
                    /* Parse APIError response */
                    val root = rawResponse.asJsonObject
                    val data = root?.getAsJsonObject("data")
                    val version = root?.get("version")?.asString ?: Versions.EWALLET_API
                    val errorCode = data?.get("code")?.asString ?: "sdk:unknown_error"
                    val description = data?.get("description")?.asString ?: "unknown error"
                    val apiError = APIError(ErrorCode.from(errorCode), description)
                    throw OMGAPIErrorException(OMGResponse(version, false, apiError))
                }
            } catch (e: JSONException) {
                throw IOException("Failed to parse JSON", e)
            }
        }
    }
}