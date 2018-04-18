package co.omisego.omisego.qrcode

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 28/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import org.amshove.kluent.shouldEqualTo
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class QRGeneratorTest {
    @Rule
    @JvmField
    val expectedEx = ExpectedException.none()!!
    private val qrGenerator by lazy { QRGenerator() }
    private val expectedSize: Int = 200
    private val expectedPayload = "Hello World"

    @Test
    fun `QRGenerator should generate QR image with the correct size`() {
        val bitmap = qrGenerator.generate(expectedPayload, expectedSize)

        bitmap.width shouldEqualTo expectedSize
        bitmap.height shouldEqualTo expectedSize
    }

    @Test
    fun `QRGenerator should throw IllegalArgumentException if the payload is empty`() {
        expectedEx.expect(IllegalArgumentException::class.java)
        expectedEx.expectMessage("Found empty contents")

        qrGenerator.generate("", expectedSize)
    }

    @Test
    fun `QRGenerator should encode payload correctly`() {
        /* Retrieve the generated QR image with payload "Hello World" */
        val qrBitmap = qrGenerator.generate(expectedPayload, expectedSize)

        /* Initial pixels with length = total pixels of qrBitmap */
        val bitmapPixels = IntArray(qrBitmap.width * qrBitmap.height)

        /* Copy all pixels from the qrBitmap to the bitmapPixels */
        qrBitmap.getPixels(bitmapPixels, 0, expectedSize, 0, 0, expectedSize, expectedSize)

        val source = RGBLuminanceSource(expectedSize, expectedSize, bitmapPixels)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

        /* Decode the result from the QR bitmap */
        val result = MultiFormatReader().decode(binaryBitmap)

        /* Validate that result payload should be equal to "Hello World" */
        result.text shouldEqualTo expectedPayload
    }
}
