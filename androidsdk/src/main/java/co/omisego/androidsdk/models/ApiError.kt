package co.omisego.androidsdk.models

import co.omisego.androidsdk.utils.ErrorCode


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/7/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

data class ApiError(val code: ErrorCode, val description: String)
