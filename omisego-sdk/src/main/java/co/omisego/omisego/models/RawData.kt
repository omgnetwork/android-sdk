package co.omisego.omisego.models

import co.omisego.omisego.utils.ErrorCode


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 11/10/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

data class RawData(val response: String?, val success: Boolean, val errorCode: ErrorCode? = null)
