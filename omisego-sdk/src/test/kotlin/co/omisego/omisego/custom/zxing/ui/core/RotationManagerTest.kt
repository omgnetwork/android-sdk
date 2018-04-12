package co.omisego.omisego.custom.zxing.ui.core

import org.amshove.kluent.shouldEqual
import org.junit.Test


/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class RotationManagerTest {
    private val rotationManager by lazy { RotationManager() }

    @Test
    fun `rotate 0 degrees should be returned the same`() {
        val result = rotationManager.rotate(byteArrayOf(0x00, 0x01, 0x02, 0x03), 2, 2, 0)
        result shouldEqual byteArrayOf(0x00, 0x01, 0x02, 0x03)
    }

    @Test
    fun `rotate 90 degrees should be rotated correctly`() {
        val result = rotationManager.rotate(byteArrayOf(0x00, 0x01, 0x02, 0x03), 2, 2, 1)
        result shouldEqual byteArrayOf(0x02, 0x00, 0x03, 0x01)
    }

    @Test
    fun `rotate 180 degrees should be returned the same`() {
        val result = rotationManager.rotate(byteArrayOf(0x00, 0x01, 0x02, 0x03), 2, 2, 2)
        result shouldEqual byteArrayOf(0x00, 0x01, 0x02, 0x03)
    }

    @Test
    fun `rotate 270 degrees should be rotated correctly`() {
        val result = rotationManager.rotate(byteArrayOf(0x00, 0x01, 0x02, 0x03), 2, 2, 3)
        result shouldEqual byteArrayOf(0x01, 0x03, 0x00, 0x02)
    }

    @Test
    fun `rotateCount with orientation null should be returned 1`() {
        rotationManager.getRotationCount(null) shouldEqual 1
    }

    @Test
    fun `rotateCount with orientation not null should be returned correctly`() {
        rotationManager.getRotationCount(0) shouldEqual 0
        rotationManager.getRotationCount(90) shouldEqual 1
        rotationManager.getRotationCount(180) shouldEqual 2
        rotationManager.getRotationCount(270) shouldEqual 3
    }
}