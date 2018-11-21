package co.omisego.omisego.network.ewallet

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 14/9/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.utils.Base64Encoder

class ClientAuthenticationHeader(
    private val apiKey: String,
    private val encoder: Base64Encoder
) : AuthenticationHeader {
    override fun create(authToken: String, userId: String?): String {
        return encoder.encode(apiKey, authToken)
    }
}
