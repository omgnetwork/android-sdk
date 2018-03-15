package co.omisego.omisego.security

import android.content.Context
import android.os.Build
import java.lang.IllegalArgumentException

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
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
 * <code>
 * val omgKeyManager = OMGKeyManager.Builder {
 *      initialize(context, "OMGShop")
 * }.build()
 *
 * val sensitiveData = "OmiseGOKey"
 *
 * val encrypted: String = omgApiClient.encrypt(context, sensitiveData)
 * val decrypted: String = omgApiClient.decrypt(context, encrypted.toByteArray())
 *
 * println(decrypted) // OmiseGOKey
 * </code>
 *
 */
class OMGKeyManager {
    private var keyManagerMarshmallow: KeyManagerMarshmallow? = null
    private var keyManagerPreMarshmallow: KeyManagerPreMarshmallow? = null

    /**
     * Build a new [OMGKeyManager].
     * Calling [Builder.initialize] is required before calling [Builder.build].
     *
     * @receiver A [Builder]'s methods.
     */
    class Builder(init: Builder.() -> Unit) {
        private lateinit var omgKeyManager: OMGKeyManager
        private var keyManagerPreMarshmallow: KeyManagerPreMarshmallow? = null
        private var keyManagerMarshmallow: KeyManagerMarshmallow? = null

        /**
         * Initialize KeyManager implementation depends on android version.
         *
         * @param context An activity or an application context
         * @param keyAlias keystore alias
         */
        fun initialize(context: Context, keyAlias: String) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                keyManagerPreMarshmallow = KeyManagerPreMarshmallow(keyAlias)
                keyManagerPreMarshmallow!!.create(context)
                val keyManagerPreference = KeyManagerPreference(context)
                keyManagerPreference.saveAESKey(keyManagerPreMarshmallow!!)
            } else {
                keyManagerMarshmallow = KeyManagerMarshmallow(keyAlias)
                keyManagerMarshmallow!!.create()
            }
        }

        /**
         * Set Initialization Vector that make it more secure.
         *
         * Note: IV should be the same value every time.
         * Instead, you can't decrypt the data that encrypted with different IV.
         *
         * See more [https://en.wikipedia.org/wiki/Initialization_vector]
         *
         * @param iv A string with 12 characters
         */
        fun setIV(iv: String) {
            if (iv.length != 12) throw IllegalArgumentException("The string should be contains 12 characters.")
            keyManagerMarshmallow?.setIV(iv)
        }

        /**
         * Create the [OMGKeyManager] instance using the configured values.
         * Note: Calling [Builder.initialize] is required before calling this.
         */
        fun build(): OMGKeyManager {
            omgKeyManager = OMGKeyManager()
            omgKeyManager.keyManagerMarshmallow = keyManagerMarshmallow
            omgKeyManager.keyManagerPreMarshmallow = keyManagerPreMarshmallow
            return omgKeyManager
        }

        init {
            init()
        }
    }

    /**
     * Encrypt the data
     *
     * @param context An activity or an application context
     * @param input The sensitive data [ByteArray]
     *
     * @return An encrypted string
     */
    fun encrypt(context: Context, input: ByteArray): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val secretKeySpec = KeyManagerPreference(context).readSecretKey(keyManagerPreMarshmallow!!) ?:
                    throw IllegalAccessException("OMGKeyManager has not been initialized!")

            keyManagerPreMarshmallow!!.encrypt(input, secretKeySpec)
        } else {
            keyManagerMarshmallow!!.encrypt(input)
        }
    }

    /**
     * Decrypt the data
     *
     * @param context An activity or an application context
     * @param input The sensitive data [ByteArray]
     *
     * @return An original string before encryption
     */
    fun decrypt(context: Context, encrypted: ByteArray): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val secretKeySpec = KeyManagerPreference(context).readSecretKey(keyManagerPreMarshmallow!!) ?:
                    throw IllegalAccessException("OMGKeyManager has not been initialized!")
            keyManagerPreMarshmallow!!.decrypt(encrypted, secretKeySpec)
        } else {
            keyManagerMarshmallow!!.decrypt(encrypted)
        }
    }
}