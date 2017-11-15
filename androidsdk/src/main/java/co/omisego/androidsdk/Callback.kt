package co.omisego.androidsdk

import co.omisego.androidsdk.models.ApiError
import co.omisego.androidsdk.models.Response


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/12/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

interface Callback<in T> {
    fun success(response: Response<T>)
    fun fail(response: Response<ApiError>)
}