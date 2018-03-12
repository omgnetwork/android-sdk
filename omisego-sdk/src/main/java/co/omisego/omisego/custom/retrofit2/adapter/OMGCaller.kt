package co.omisego.omisego.custom.retrofit2.adapter

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 8/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.ErrorCode
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.custom.Callback
import co.omisego.omisego.exception.OMGAPIErrorException
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import retrofit2.Call
import retrofit2.Response
import java.net.HttpURLConnection

internal class OMGCaller<T>(private val call: Call<OMGResponse<T>>) : OMGCall<OMGResponse<T>> {
    override fun cancel() = call.cancel()
    override fun clone() = OMGCaller(call.clone())
    override fun execute(): Response<OMGResponse<T>> = call.execute()
    override fun enqueue(callback: Callback<OMGResponse<T>>) {
        call.enqueue(object : retrofit2.Callback<OMGResponse<T>> {
            override fun onFailure(call: Call<OMGResponse<T>>, t: Throwable) {
                when (t) {
                    is OMGAPIErrorException -> callback.fail(t.response)
                    else -> {
                        val apiError = APIError(ErrorCode.SDK_NETWORK_ERROR, t.localizedMessage)
                        callback.fail(OMGResponse(Versions.EWALLET_API, false, apiError))
                    }
                }
            }

            override fun onResponse(call: Call<OMGResponse<T>>, response: Response<OMGResponse<T>>) {
                val body = response.body()
                if (response.isSuccessful && body != null) return callback.success(body)
                val apiError = when {
                    response.code() == HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                        APIError(ErrorCode.SERVER_INTERNAL_SERVER_ERROR, "The EWallet API was 500 Internal Server Error")
                    }
                    body == null -> {
                        APIError(ErrorCode.SDK_PARSE_ERROR, "The response body was null")
                    }
                    else -> {
                        APIError(ErrorCode.SDK_UNEXPECTED_ERROR, "Unexpected Error")
                    }
                }
                callback.fail(OMGResponse(Versions.EWALLET_API, false, apiError))
            }
        })
    }
}