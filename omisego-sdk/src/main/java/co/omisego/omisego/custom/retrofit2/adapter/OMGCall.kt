package co.omisego.omisego.custom.retrofit2.adapter

import co.omisego.omisego.custom.Callback
import retrofit2.Response

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 8/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

interface OMGCall<T> {
    fun cancel()
    fun clone(): OMGCall<T>
    fun execute(): Response<T>
    fun enqueue(callback: Callback<T>)
}