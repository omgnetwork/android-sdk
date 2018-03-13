package co.omisego.omisego.custom.retrofit2.adapter

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 9/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.OMGResponse
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class OMGCallAdapter<R>(
        private val responseType: TypeToken<R>
) : CallAdapter<OMGResponse<R>, OMGCall<R>> {

    override fun adapt(call: Call<OMGResponse<R>>): OMGCall<R> = OMGCaller(call)

    override fun responseType(): Type = OMGResponseType(responseType.type)

    companion object {
        private class OMGResponseType(private val responseType: Type) : ParameterizedType {
            override fun getRawType(): Type = OMGResponse::class.java
            override fun getOwnerType(): Type? = null
            override fun getActualTypeArguments(): Array<Type> = arrayOf(responseType)
        }
    }
}
