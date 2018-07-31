package co.omisego.omisego.security

/**
 * OmiseGO
 *
 * Created by Yannick Badoual on 2/28/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.content.Context
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher

internal abstract class KeyManager(private val keyHolder: KeyHolder) {

    protected val keyStore: KeyStore
        get() = keyHolder.keyStore
    protected val keyAlias: String
        get() = keyHolder.keyAlias

    protected abstract val encryptCipher: Cipher
    protected abstract val decryptCipher: Cipher

    open val b64Mode: Int
        get() = Base64.DEFAULT

    companion object {
        const val ANDROID_KEY_STORE = "AndroidKeyStore"
    }

    fun create(context: Context) {
        if (!keyStore.containsAlias(keyAlias)) {
            generateKey(context)
        }
    }

    abstract fun generateKey(context: Context)

    abstract fun initCipher(cipher: Cipher, opmode: Int)

    // Encrypt data
    fun encrypt(input: ByteArray): String {
        val encodedBytes = synchronized(encryptCipher) {
            encryptCipher.doFinal(input)
        }
        return Base64.encodeToString(encodedBytes, b64Mode)
    }

    // Decrypt data
    fun decrypt(encrypted: ByteArray): String {
        val decoded = Base64.decode(encrypted, b64Mode)
        val decodedBytes = synchronized(decryptCipher) {
            decryptCipher.doFinal(decoded)
        }
        return String(decodedBytes)
    }
}