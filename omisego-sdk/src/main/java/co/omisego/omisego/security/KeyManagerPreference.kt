package co.omisego.omisego.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import co.omisego.omisego.R
import java.security.SecureRandom
import javax.crypto.spec.SecretKeySpec


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

internal class KeyManagerPreference(private val context: Context) {
    companion object {
        val ENCRYPTED_AES_KEY = "encrypted_aes_key"
    }

    private val sharePref: SharedPreferences by lazy {
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    }

    fun saveAESKey(keyManager: KeyManagerPreMarshmallow) {
        val key = sharePref.getString(ENCRYPTED_AES_KEY, "")

        if (key.isEmpty()) {
            val bytes = ByteArray(16)
            val secureRandom = SecureRandom()
            secureRandom.nextBytes(bytes)

            val encryptedKey = keyManager.rsaEncrypt(bytes)
            val encryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.NO_WRAP)

            sharePref.edit().putString(ENCRYPTED_AES_KEY, encryptedKeyB64).apply()
        }
    }

    fun readSecretKey(keyManager: KeyManagerPreMarshmallow): SecretKeySpec? {
        val encryptedKeyB64 = sharePref.getString(ENCRYPTED_AES_KEY, null)

        if (encryptedKeyB64 != null) {
            val encryptedKey = Base64.decode(encryptedKeyB64, Base64.NO_WRAP)
            val key = keyManager.rsaDecrypt(encryptedKey)
            return SecretKeySpec(key, "AES")
        }
        return null
    }
}