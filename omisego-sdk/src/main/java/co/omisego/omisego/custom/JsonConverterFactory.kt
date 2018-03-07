package co.omisego.omisego.custom

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import com.google.gson.Gson
import com.google.gson.JsonElement
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

internal class JsonConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *> {
        return JsonConverter.INSTANCE
    }

    internal class JsonConverter : Converter<ResponseBody, JsonElement> {
        @Throws(IOException::class)
        override fun convert(responseBody: ResponseBody): JsonElement {
            try {
                return Gson().fromJson(responseBody.string(), JsonElement::class.java)
            } catch (e: JSONException) {
                throw IOException("Failed to parse JSON", e)
            }

        }

        companion object {
            val INSTANCE = JsonConverter()
        }
    }
}