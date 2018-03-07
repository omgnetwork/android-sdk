package co.omisego.omisego.custom

import co.omisego.omisego.constant.ErrorCode
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.model.ApiError
import co.omisego.omisego.model.OMGResponse
import com.google.gson.Gson
import com.google.gson.JsonElement
import retrofit2.Response
import java.lang.reflect.Type


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 6/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

internal class Serializer {
    fun failure(t: Throwable): OMGResponse<ApiError> {
        val apiError = ApiError(ErrorCode.SDK_NETWORK_ERROR, t.localizedMessage)
        return OMGResponse(Versions.EWALLET_API, false, apiError)
    }

    fun failure(response: Response<JsonElement>): OMGResponse<ApiError> {
        return when {
            !response.isSuccessful -> {
                val apiError = ApiError(ErrorCode.SDK_PARSE_ERROR, "Cannot parse the response")
                OMGResponse(Versions.EWALLET_API, false, apiError)
            }
            else -> {
                val root = response.body()?.asJsonObject
                val data = root?.getAsJsonObject("data")
                val version = root?.get("version")?.asString ?: Versions.EWALLET_API
                val errorCode = data?.get("code")?.asString ?: "sdk:unknown_error"
                val description = data?.get("description")?.asString ?: "unknown error"
                val apiError = ApiError(ErrorCode.from(errorCode), description)
                OMGResponse(version, false, apiError)
            }
        }
    }

    fun <T> success(response: Response<JsonElement>, type: Type): T {
        return Gson().fromJson<T>(response.body(), type)
    }
}
