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
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

internal class KeyManagerPreference(context: Context, private val rsaCipher: RSACipher) {
    companion object {
        const val ENCRYPTED_AES_KEY = "encrypted_aes_key"
    }

    private val sharePref: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name),
                                                                            Context.MODE_PRIVATE)

    fun saveAESKeyIfAbsent() {
        if (sharePref.contains(ENCRYPTED_AES_KEY)) return

        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)

        val encryptedKey = rsaCipher.encrypt(bytes)
        val encryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.NO_WRAP)

        sharePref.edit().putString(ENCRYPTED_AES_KEY, encryptedKeyB64).apply()
    }

    fun readSecretKey(): SecretKeySpec {
        val encryptedKeyB64 = sharePref.getString(ENCRYPTED_AES_KEY, null)
                ?: throw IllegalStateException("Key does not exist")
        val encryptedKey = Base64.decode(encryptedKeyB64, Base64.NO_WRAP)
        val key = rsaCipher.decrypt(encryptedKey)
        return SecretKeySpec(key, "AES")
    }
}