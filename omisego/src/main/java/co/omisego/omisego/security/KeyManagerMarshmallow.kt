package co.omisego.omisego.security

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import android.util.Base64
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/12/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

// IV or Initialization Vector used in encryption and decryption. It must be the same value and contain 12 characters.
internal class KeyManagerMarshmallow(private var keyAlias: String, private var iv: String = String(ByteArray(12))) {
    private lateinit var keyStore: KeyStore

    companion object {
        val ANDROID_KEY_STORE = "AndroidKeyStore"
        val AES_MODE = "AES/GCM/NoPadding"
    }

    // Generate key function for Android version 6.0 Marshmallow or above
    @RequiresApi(Build.VERSION_CODES.M)
    fun create() {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)

            val spec = KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(false)
                    .build()

            keyGenerator.init(spec)
            keyGenerator.generateKey()
        }
    }

    fun setIV(iv: String) {
        this.iv = iv
    }

    private fun getSecretKey(): Key = keyStore.getKey(keyAlias, null)

    // Encrypt data
    fun encrypt(input: ByteArray): String {
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), GCMParameterSpec(128, iv.toByteArray()))
        val encodedBytes: ByteArray = cipher.doFinal(input)
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT)
    }

    // Decrypt data
    fun decrypt(encrypted: ByteArray): String {
        val c = Cipher.getInstance(AES_MODE)
        c.init(Cipher.DECRYPT_MODE, getSecretKey(), GCMParameterSpec(128, iv.toByteArray()))
        val decoded = Base64.decode(encrypted, Base64.DEFAULT)
        val decodedBytes = c.doFinal(decoded)
        return String(decodedBytes)
    }
}