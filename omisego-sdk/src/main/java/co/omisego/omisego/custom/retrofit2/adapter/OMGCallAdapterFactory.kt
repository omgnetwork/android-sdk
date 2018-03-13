package co.omisego.omisego.custom.retrofit2.adapter

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 8/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class OMGCallAdapterFactory() : CallAdapter.Factory() {

    companion object {
        fun create(): OMGCallAdapterFactory {
            return OMGCallAdapterFactory()
        }
    }

    override fun get(returnType: Type, annotations: Array<out Annotation>?, retrofit: Retrofit): CallAdapter<*, *>? {
        return when {
            getRawType(returnType) != OMGCall::class.java -> return null
            returnType !is ParameterizedType -> throw IllegalStateException("OMGCall must have a generic type")
            else -> {
                val responseType = getParameterUpperBound(0, returnType)
                OMGCallAdapter(TypeToken.get(responseType))
            }
        }
    }
}
