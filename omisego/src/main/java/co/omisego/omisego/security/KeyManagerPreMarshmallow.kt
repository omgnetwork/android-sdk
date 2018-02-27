package co.omisego.omisego.security

import android.content.Context
import android.security.KeyPairGeneratorSpec
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal


/**
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/12/2017 AD.
 * Copyright © 2017 OmiseGO. All rights reserved.
 */

internal class KeyManagerPreMarshmallow(private var keyAlias: String) {
    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE)

    companion object {
        val ANDROID_KEY_STORE = "AndroidKeyStore"
        val RSA_MODE = "RSA/ECB/PKCS1Padding"
        val AES_MODE = "AES/ECB/PKCS7Padding"
    }

    fun create(context: Context) {
        if (!keyStore.containsAlias(keyAlias)) {
            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 25)

            val spec = KeyPairGeneratorSpec.Builder(context)
                    .setAlias(this.keyAlias)
                    .setSubject(X500Principal("CN=${keyAlias}"))
                    .setSerialNumber(BigInteger.TEN)
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .build()

            val kpg = KeyPairGenerator.getInstance("RSA", KeyManagerMarshmallow.ANDROID_KEY_STORE)
            kpg.initialize(spec)
            kpg.generateKeyPair()
        }
    }

    // Encrypt the AES secret with RSA
    fun rsaEncrypt(secret: ByteArray): ByteArray? {
        val privateKeyEntry: KeyStore.PrivateKeyEntry = keyStore.getEntry(keyAlias, null) as KeyStore.PrivateKeyEntry

        val inputCipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL")
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.certificate.publicKey)
        val outputStream = ByteArrayOutputStream()
        val cipherOutputStream = CipherOutputStream(outputStream, inputCipher)
        cipherOutputStream.write(secret)
        cipherOutputStream.close()

        val encrypted = outputStream.toByteArray()
        return encrypted
    }

    // Decrypt the AES secret with RSA
    fun rsaDecrypt(encrypted: ByteArray): ByteArray {
        val privateKeyEntry = keyStore.getEntry(keyAlias, null) as KeyStore.PrivateKeyEntry
        val output = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL")

        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)

        val cipherInputStream = CipherInputStream(ByteArrayInputStream(encrypted), output)

        val values = mutableListOf<Byte>()
        while (true) {
            val nextByte = cipherInputStream.read()
            if (nextByte == -1) break
            values.add(nextByte.toByte())
        }

        val bytes = ByteArray(values.size)
                .mapIndexed { index, _ -> values[index] }
                .toByteArray()

        return bytes
    }

    // Encrypt data
    fun encrypt(input: ByteArray, secretKey: SecretKeySpec): String {
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encodedBytes = cipher.doFinal(input)
        val encryptedBase64Encoded = Base64.encodeToString(encodedBytes, Base64.NO_WRAP)
        return encryptedBase64Encoded
    }

    // Decrypt data
    fun decrypt(encrypted: ByteArray, secretKey: SecretKeySpec): String {
        val cipher = Cipher.getInstance(AES_MODE, "BC")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBase64 = Base64.decode(encrypted, Base64.NO_WRAP)
        val decodedBytes = cipher.doFinal(decodedBase64)
        return String(decodedBytes)
    }

    init {
        keyStore.load(null)
    }
}