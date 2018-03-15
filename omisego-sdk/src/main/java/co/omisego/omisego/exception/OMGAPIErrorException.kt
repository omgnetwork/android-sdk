package co.omisego.omisego.exception

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/20/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse

class OMGAPIErrorException(error: OMGResponse<APIError>) : Exception() {
    override val message: String = error.toString()
    val response: OMGResponse<APIError> = error
}