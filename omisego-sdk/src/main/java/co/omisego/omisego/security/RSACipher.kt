package co.omisego.omisego.security

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream

/**
 * OmiseGO
 *
 * Created by Yannick Badoual on 2/28/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

internal class RSACipher(private val keyHolder: KeyHolder) {

    private val keyStore: KeyStore
        get() = keyHolder.keyStore
    private val keyAlias: String
        get() = keyHolder.keyAlias
    private val privateKeyEntry: KeyStore.PrivateKeyEntry
        get() = keyStore.getEntry(keyAlias, null) as KeyStore.PrivateKeyEntry

    private val cipher: Cipher
        get() = Cipher.getInstance(RSA_MODE, RSA_PROVIDER)

    companion object {
        private const val RSA_MODE = "RSA/ECB/PKCS1Padding"
        private const val RSA_PROVIDER = "AndroidOpenSSL"
    }

    // Encrypt the AES secret with RSACipher
    fun encrypt(secret: ByteArray): ByteArray {
        val inputCipher = cipher
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.certificate.publicKey)

        val outputStream = ByteArrayOutputStream()
        val cipherOutputStream = CipherOutputStream(outputStream, inputCipher)
        cipherOutputStream.write(secret)
        cipherOutputStream.close()

        return outputStream.toByteArray()
    }

    // Decrypt the AES secret with RSACipher
    fun decrypt(encrypted: ByteArray): ByteArray {
        val output = cipher
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)

        val cipherInputStream = CipherInputStream(ByteArrayInputStream(encrypted), output)

        val values = mutableListOf<Byte>()
        while (true) {
            val nextByte = cipherInputStream.read()
            if (nextByte == -1) break
            values.add(nextByte.toByte())
        }

        return ByteArray(values.size)
                .mapIndexed { index, _ -> values[index] }
                .toByteArray()
    }
}