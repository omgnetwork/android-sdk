package co.omisego.omisego.security

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

// IV or Initialization Vector used in encryption and decryption. It must be the same value and contain 12 characters.
@RequiresApi(Build.VERSION_CODES.M)
internal class KeyManagerMarshmallow(
    keyHolder: KeyHolder,
    iv: String = String(ByteArray(12))
) : KeyManager(keyHolder) {

    override val encryptCipher: Cipher
        get() = Cipher.getInstance(AES_MODE).also { initCipher(it, Cipher.ENCRYPT_MODE) }
    override val decryptCipher: Cipher
        get() = Cipher.getInstance(AES_MODE).also { initCipher(it, Cipher.DECRYPT_MODE) }

    private val secretKey: Key
        get() = keyStore.getKey(keyAlias, null)

    private val gcmParameterSpec = GCMParameterSpec(128, iv.toByteArray())

    companion object {
        const val AES_MODE = "AES/GCM/NoPadding"
    }

    // Generate key function for Android version 6.0 Marshmallow or above
    override fun generateKey(context: Context) {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEY_STORE
        )

        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(false)
            .build()

        keyGenerator.init(spec)
        keyGenerator.generateKey()
    }

    override fun initCipher(cipher: Cipher, opmode: Int) {
        if (opmode !in arrayOf(Cipher.ENCRYPT_MODE, Cipher.DECRYPT_MODE)) {
            throw IllegalArgumentException("Unsupported opmode $opmode")
        }
        cipher.init(opmode, secretKey, gcmParameterSpec)
    }
}