package co.omisego.omisego.custom

import co.omisego.omisego.model.OMGResponse
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import co.omisego.omisego.custom.Callback as OMGCallback


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

internal class CallbackManager<out T>(private val serializer: Serializer, private val type: Type) {
    companion object {
        inline fun <reified T> newInstance(): CallbackManager<T> {
            val type = object : TypeToken<OMGResponse<T>>() {}.type
            return CallbackManager(Serializer(), type)
        }
    }

    fun transform(callback: OMGCallback<T>) = object : Callback<JsonElement> {
        override fun onFailure(call: Call<JsonElement>, t: Throwable) = callback.fail(serializer.failure(t))

        override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
            val body = response.body()?.asJsonObject
            val success = body?.get("success")?.asBoolean ?: false
            if (response.isSuccessful && success) {
                callback.success(serializer.success(response, type))
            } else {
                callback.fail(serializer.failure(response))
            }
        }
    }
}
