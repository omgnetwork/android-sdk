package co.omisego.omisego.custom.retrofit2.adapter

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 8/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.model.OMGResponse
import retrofit2.Call
import retrofit2.Response

internal class OMGCaller<T>(private val call: Call<OMGResponse<T>>) : OMGCall<T> {
    override fun cancel() = call.cancel()
    override fun clone() = OMGCaller(call.clone())
    override fun execute(): Response<OMGResponse<T>> = call.execute()
    override fun enqueue(callback: OMGCallback<T>) = call.enqueue(OMGCallbackWrapper(callback))
}
