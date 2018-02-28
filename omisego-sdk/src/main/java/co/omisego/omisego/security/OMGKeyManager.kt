package co.omisego.omisego.security

import android.content.Context
import android.os.Build
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.security.KeyStore

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/12/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

/**
 * A utility class that makes encryption and decryption process easier.
 * The implementation follows android security best practice using [java.security.KeyStore] pattern.
 *
 * The implementation is quite different between API level lower than 23 and API level 23 or higher.
 * For API level 23 and higher, the implementation is relatively easy because the API generates random AES keys for us.
 * For Android API versions lower than 23, The [KeyGenParameterSpec] isn't available.
 * Instead, we will use the [KeyPairGeneratorSpec] API.
 * Read more : [https://developer.android.com/training/articles/keystore.html]
 *
 * Create instances using [OMGKeyManager.Builder], pass [Context] and [keyAlias] to generate an implementation.
 *
 * For example,
 *
 * <code>
 * val omgKeyManager = OMGKeyManager.Builder {
 *      keyAlias = "OMGShop"
 * }.build(context)
 *
 * val sensitiveData = "OmiseGOKey"
 *
 * val encrypted: String = omgApiClient.encrypt(sensitiveData)
 * val decrypted: String = omgApiClient.decrypt(encrypted.toByteArray())
 *
 * println(decrypted) // OmiseGOKey
 * </code>
 *
 */
class OMGKeyManager private constructor(private var keyManager: KeyManager) {

    /**
     * Build a new [OMGKeyManager].
     *
     * @receiver A [Builder]'s methods.
     */
    class Builder(init: Builder.() -> Unit) {
        private lateinit var keyManager: KeyManager

        var keyAlias: String = ""
        /**
         * Set Initialization Vector that make it more secure.
         *
         * Note: IV should be the same value every time and always 12 characters long.
         * Instead, you can't decrypt the data that encrypted with different IV.
         *
         * See more [https://en.wikipedia.org/wiki/Initialization_vector]
         */
        var iv: String = String(ByteArray(12))
            set(value) {
                if (value.length != 12) throw IllegalArgumentException("The string should be contains 12 characters.")
                field = value
            }


        init {
            init()
        }

        /**
         * Create the [OMGKeyManager] instance using the configured values.
         * @param context An activity or application context
         */
        fun build(context: Context): OMGKeyManager {
            if (keyAlias.isBlank()) throw IllegalStateException("keyAlias not set")

            val keyStore = KeyStore.getInstance(KeyManager.ANDROID_KEY_STORE)
            val keyHolder = KeyHolder(keyStore, keyAlias)

            keyManager =
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        val rsaCipher = RSACipher(keyHolder)
                        val keyManagerPreference = KeyManagerPreference(context, rsaCipher)
                        keyManagerPreference.saveAESKeyIfAbsent()

                        KeyManagerPreMarshmallow(keyHolder, keyManagerPreference)
                    } else {
                        KeyManagerMarshmallow(keyHolder, iv)
                    }
            keyManager.create(context)

            return OMGKeyManager(keyManager)
        }
    }

    /**
     * Encrypt the data
     *
     * @param input The sensitive data [ByteArray]
     *
     * @return An encrypted string
     */
    fun encrypt(input: ByteArray): String = keyManager.encrypt(input)

    /**
     * Decrypt the data
     *
     * @param encrypted The sensitive data [ByteArray]
     *
     * @return An original string before encryption
     */
    fun decrypt(encrypted: ByteArray): String = keyManager.decrypt(encrypted)
}