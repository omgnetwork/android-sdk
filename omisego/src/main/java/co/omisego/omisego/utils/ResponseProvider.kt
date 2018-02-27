package co.omisego.omisego.utils

import co.omisego.omisego.models.ApiError
import co.omisego.omisego.models.General
import co.omisego.omisego.models.Response


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/17/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

class ResponseProvider {
    fun <T> success(general: General, handler: (String) -> T): Response<T> {
        return Response(general.version,
                true,
                Serializer(handler).serialize(general.data.toString()))
    }

    fun failure(general: General): Response<ApiError> {
        return Response(general.version,
                false,
                Serializer(ParseStrategy.API_ERROR).serialize(general.data.toString()))
    }
}