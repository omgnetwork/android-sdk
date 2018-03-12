package co.omisego.omisego.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.util.Base64

object EncryptionHelper {
    /**
     * Encrypt the string with base64
     *
     * Currently there're two cases to encrypt Base64 in the SDK which is the following.
     * 1. access_key:secret_key for the OMGServer
     * 2. api_key:authentication_token for the OMGClient
     *
     * @param secrets Authentication parts which the encryption needed
     * @return An encrypted string
     */
    fun encryptBase64(vararg secrets: String): String {
        val encrypted: String = secrets.joinToString(":")
        return String(Base64.encode(encrypted.toByteArray(), Base64.NO_WRAP))
    }
}
