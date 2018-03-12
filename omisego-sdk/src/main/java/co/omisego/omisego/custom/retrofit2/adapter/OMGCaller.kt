package co.omisego.omisego.custom.retrofit2.adapter

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 8/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.ErrorCode
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.exception.OMGAPIErrorException
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import retrofit2.Call
import retrofit2.Response
import java.net.HttpURLConnection

internal class OMGCaller<T>(private val call: Call<T>) : OMGCall<T> {
    override fun cancel() = call.cancel()
    override fun clone() = OMGCaller(call.clone())
    override fun execute(): Response<T> = call.execute()
    override fun enqueue(callback: OMGCallback<T>) {
        call.enqueue(object : retrofit2.Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                when (t) {
                    is OMGAPIErrorException -> callback.fail(t.response)
                    else -> {
                        val apiError = APIError(ErrorCode.SDK_NETWORK_ERROR, t.localizedMessage)
                        callback.fail(OMGResponse(Versions.EWALLET_API, false, apiError))
                    }
                }
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = try {
                    response.body() as OMGResponse<T>?
                } catch (e: ClassCastException) {
                    val apiError = APIError(ErrorCode.SDK_PARSE_ERROR, e.message!!)
                    callback.fail(OMGResponse(Versions.EWALLET_API, false, apiError))
                    return
                }

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