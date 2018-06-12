package co.omisego.omisego.security

import android.annotation.SuppressLint
import android.content.Context
import android.security.KeyPairGeneratorSpec
import android.util.Base64
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.util.Calendar
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal

/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

@SuppressLint("GetInstance")
internal class KeyManagerPreMarshmallow(
    keyHolder: KeyHolder,
    private val keyManagerPreference: KeyManagerPreference
) : KeyManager(keyHolder) {

    override val encryptCipher: Cipher by lazy { Cipher.getInstance(AES_MODE) }
    override val decryptCipher: Cipher by lazy { Cipher.getInstance(AES_MODE, "BC") }

    override val b64Mode: Int
        get() = Base64.NO_WRAP

    private val secretKey: SecretKeySpec?
        get() = keyManagerPreference.readSecretKey()

    companion object {
        private const val AES_MODE = "AES/ECB/PKCS7Padding"
    }

    override fun generateKey(context: Context) {
        val start = Calendar.getInstance()
        val end = Calendar.getInstance().apply {
            add(Calendar.YEAR, 25)
        }

        val spec = KeyPairGeneratorSpec.Builder(context)
            .setAlias(keyAlias)
            .setSubject(X500Principal("CN=$keyAlias"))
            .setSerialNumber(BigInteger.TEN)
            .setStartDate(start.time)
            .setEndDate(end.time)
            .build()

        with(KeyPairGenerator.getInstance("RSA", ANDROID_KEY_STORE)) {
            initialize(spec)
            generateKeyPair()
        }
    }

    override fun initCipher(cipher: Cipher, opmode: Int) {
        if (opmode !in arrayOf(Cipher.ENCRYPT_MODE, Cipher.DECRYPT_MODE)) {
            throw IllegalArgumentException("Unsupported opmode $opmode")
        }
        cipher.init(opmode, secretKey)
    }
}