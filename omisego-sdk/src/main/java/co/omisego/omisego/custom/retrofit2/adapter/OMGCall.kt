package co.omisego.omisego.custom.retrofit2.adapter

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 8/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.model.OMGResponse
import retrofit2.Response

interface OMGCall<T> {
    fun cancel()
    fun clone(): OMGCall<T>
    fun execute(): Response<OMGResponse<T>>
    fun enqueue(callback: OMGCallback<T>)
}
