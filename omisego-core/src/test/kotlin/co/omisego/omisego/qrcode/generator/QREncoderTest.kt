package co.omisego.omisego.qrcode.generator

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 20/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import com.google.zxing.common.BitMatrix
import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class QREncoderTest {

    private val qrEncoder by lazy { QREncoder() }

    @Test
    fun `Bitmap pixels should be equal to BitMatrix pixel`() {
        val matrix = BitMatrix(256, 256)
        val bitmap = qrEncoder.createBitmap(matrix)

        bitmap.height shouldEqual matrix.height
        bitmap.width shouldEqual matrix.height
        bitmap.width shouldEqual bitmap.height
    }

    @Test
    fun `Bitmap should have a correct black and white pixels`() {
        val dimension = 64
        val matrix = BitMatrix(dimension).apply {
            set(0, 0)
            set(dimension - 1, dimension - 1)
        }

        val bitmap = qrEncoder.createBitmap(matrix)

        bitmap.getPixel(0, 0) shouldEqual QREncoder.BLACK
        bitmap.getPixel(dimension - 1, dimension - 1) shouldEqual QREncoder.BLACK

        for (i in 0 until dimension) {
            for (j in 1 until dimension - 1) {
                bitmap.getPixel(i, j) shouldEqual QREncoder.WHITE
            }
        }
    }
}
