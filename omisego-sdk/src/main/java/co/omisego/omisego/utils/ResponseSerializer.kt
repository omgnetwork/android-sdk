package co.omisego.omisego.utils

import co.omisego.omisego.models.ApiError
import co.omisego.omisego.models.Response
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/3/2018 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class ResponseSerializer<out T> {
    fun default(callback: co.omisego.omisego.Callback<T>): Callback<JsonElement> {
        return object : retrofit2.Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                val apiError = ApiError(ErrorCode.SDK_NETWORK_ERROR, t.localizedMessage)
                val response = Response("1", false, apiError)
                callback.fail(response)
            }

            override fun onResponse(call: Call<JsonElement>, response: retrofit2.Response<JsonElement>) {
                when {
                    !response.isSuccessful -> {
                        val apiError = ApiError(ErrorCode.SDK_UNKNOWN_ERROR, "Something went wrong")
                        val failResponse = Response("1", false, apiError)
                        callback.fail(failResponse)
                    }
                    else -> {
                        val gson = Gson()
                        val jsonObject = response.body()!!.asJsonObject
                        val success = jsonObject.get("success").asBoolean
                        if (success) {
                            val token = object : TypeToken<Response<T>>() {}.type
                            val successResponse = gson.fromJson<Response<T>>(response.body(), token)
                            callback.success(successResponse)
                        } else {
                            val token = object : TypeToken<Response<ApiError>>() {}.type
                            val failResponse = gson.fromJson<Response<ApiError>>(response.body(), token)
                            callback.fail(failResponse)
                        }
                    }
                }
            }
        }
    }
}