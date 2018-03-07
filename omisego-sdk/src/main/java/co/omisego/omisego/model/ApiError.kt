package co.omisego.omisego.model

import co.omisego.omisego.constant.ErrorCode


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/7/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

/**
 * Represents an error caused by the SDK or the OmiseGO API.
 *
 * @param code An error code contained in [ErrorCode]
 * @param description The description associated with the received error code
 */
data class ApiError(val code: ErrorCode, val description: String)
