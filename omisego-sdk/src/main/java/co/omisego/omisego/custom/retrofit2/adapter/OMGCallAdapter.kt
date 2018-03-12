package co.omisego.omisego.custom.retrofit2.adapter

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 9/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.OMGResponse
import com.google.gson.TypeAdapter
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class OMGCallAdapter<R> (private val responseType: Type, private val adapter: TypeAdapter<R>) : CallAdapter<OMGResponse<R>, OMGCall<OMGResponse<R>>> {
    override fun adapt(call: Call<OMGResponse<R>>): OMGCall<OMGResponse<R>> {
        return OMGCaller(call)
    }

    override fun responseType() = responseType
}