package co.omisego.omisego.custom.retrofit2.adapter

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 13/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.exception.OMGAPIErrorException
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.concurrent.Executor

internal class OMGCallbackWrapper<T>(private val callback: OMGCallback<T>, private val callbackExecutor: Executor) : Callback<OMGResponse<T>> {
    override fun onFailure(call: Call<OMGResponse<T>>, t: Throwable) {
        when (t) {
            is OMGAPIErrorException -> {
                callbackExecutor.execute { callback.fail(t.response) }
            }
            else -> {
                val apiError = APIError(ErrorCode.SDK_NETWORK_ERROR, t.localizedMessage)
                callbackExecutor.execute {
                    callback.fail(OMGResponse(Versions.EWALLET_API, false, apiError))
                }
            }
        }
    }

    override fun onResponse(call: Call<OMGResponse<T>>, response: Response<OMGResponse<T>>) {
        val body = response.body()
        val errorBody = response.errorBody()
        if (response.isSuccessful && body != null) {
            callbackExecutor.execute {
                callback.success(body)
            }
            return
        }

        val apiError = when {
            response.code() == HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                APIError(ErrorCode.SERVER_INTERNAL_SERVER_ERROR,
                        "The EWallet API was 500 Internal Server Error")
            }
            errorBody != null -> {
                APIError(ErrorCode.SERVER_UNKNOWN_ERROR, errorBody.string())
            }
            else -> {
                APIError(ErrorCode.SDK_UNEXPECTED_ERROR, "Unexpected Error")
            }
        }
        callbackExecutor.execute {
            callback.fail(OMGResponse(Versions.EWALLET_API, false, apiError))
        }
    }
}
