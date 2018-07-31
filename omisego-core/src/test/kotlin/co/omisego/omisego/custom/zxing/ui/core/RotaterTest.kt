package co.omisego.omisego.custom.zxing.ui.core

import co.omisego.omisego.qrcode.scanner.utils.Rotater
import org.amshove.kluent.shouldEqual
import org.junit.Test

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class RotaterTest {
    private val rotationManager by lazy { Rotater() }

    @Test
    fun `rotate 0 degrees should be returned the same`() {
        val result = rotationManager.rotate(byteArrayOf(0x00, 0x01, 0x02, 0x03), 2, 2, 0)
        result shouldEqual byteArrayOf(0x00, 0x01, 0x02, 0x03)
    }

    @Test
    fun `rotate 90 degrees should be rotated correctly`() {
        val result = rotationManager.rotate(byteArrayOf(0x00, 0x01, 0x02, 0x03), 2, 2, 90)
        result shouldEqual byteArrayOf(0x02, 0x00, 0x03, 0x01)
    }

    @Test
    fun `rotate 180 degrees should be returned the same`() {
        val result = rotationManager.rotate(byteArrayOf(0x00, 0x01, 0x02, 0x03), 2, 2, 180)
        result shouldEqual byteArrayOf(0x03, 0x02, 0x01, 0x00)
    }

    @Test
    fun `rotate 270 degrees should be rotated correctly`() {
        val result = rotationManager.rotate(byteArrayOf(0x00, 0x01, 0x02, 0x03), 2, 2, 270)
        result shouldEqual byteArrayOf(0x01, 0x03, 0x00, 0x02)
    }
}