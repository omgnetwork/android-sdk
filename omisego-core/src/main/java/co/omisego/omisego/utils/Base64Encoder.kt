package co.omisego.omisego.utils

import android.util.Base64

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 17/8/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class Base64Encoder {
    fun encode(vararg parts: String) = String(Base64.encode(parts.joinToString(":").toByteArray(), Base64.NO_WRAP))
}
