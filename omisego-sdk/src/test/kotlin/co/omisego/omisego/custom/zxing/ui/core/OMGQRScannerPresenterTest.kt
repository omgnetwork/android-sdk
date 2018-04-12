package co.omisego.omisego.custom.zxing.ui.core

import org.amshove.kluent.shouldEqual
import org.junit.Test

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class OMGQRScannerPresenterTest {
    private val omgQRScannerPresenter: OMGQRScannerPresenter = OMGQRScannerPresenter()
    private val sampleByteArray = byteArrayOf(0x00, 0x01, 0x02, 0x03)

    @Test
    fun `OMGQRScanner should be adjusted rotation correctly`() {
        val result0 = omgQRScannerPresenter.adjustRotation(sampleByteArray, false, 2 to 2, 0)
        result0 shouldEqual byteArrayOf(0x00, 0x01, 0x02, 0x03)

        val result90 = omgQRScannerPresenter.adjustRotation(sampleByteArray, true, 2 to 2, 90)
        result90 shouldEqual byteArrayOf(0x02, 0x00, 0x03, 0x01)

        val result270 = omgQRScannerPresenter.adjustRotation(sampleByteArray, true, 2 to 2, 270)
        result270 shouldEqual byteArrayOf(0x01, 0x03, 0x00, 0x02)
    }
}