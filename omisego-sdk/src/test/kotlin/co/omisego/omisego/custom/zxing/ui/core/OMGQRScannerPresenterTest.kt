package co.omisego.omisego.custom.zxing.ui.core

import android.graphics.Rect
import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class OMGQRScannerPresenterTest {
    private val omgQRScannerPresenter: OMGQRScannerPresenter = OMGQRScannerPresenter()
    private val sampleByteArray = byteArrayOf(0x00, 0x01, 0x02, 0x03)

    @Test
    fun `OMGQRScanner should be adjusted the image rotation data correctly`() {
        val result0 = omgQRScannerPresenter.adjustRotation(sampleByteArray, false, 2 to 2, 0)
        result0 shouldEqual byteArrayOf(0x00, 0x01, 0x02, 0x03)

        val result90 = omgQRScannerPresenter.adjustRotation(sampleByteArray, true, 2 to 2, 90)
        result90 shouldEqual byteArrayOf(0x02, 0x00, 0x03, 0x01)

        val result180 = omgQRScannerPresenter.adjustRotation(sampleByteArray, false, 2 to 2, 180)
        result180 shouldEqual byteArrayOf(0x03, 0x02, 0x01, 0x00)

        val result270 = omgQRScannerPresenter.adjustRotation(sampleByteArray, true, 2 to 2, 270)
        result270 shouldEqual byteArrayOf(0x01, 0x03, 0x00, 0x02)
    }

    @Test
    fun `OMGQRScanner should be adjusted frame to fit in the preview size correctly`() {
        /* A square rectangle with size 256px at the (100,100) */
        val framingRect = Rect(100, 100, 356, 356)

        /* The dimension of the raw image. let's say 1600x1200 px */
        val scannerSize = 1600 to 1200

        /* The preview size in the layout. let's say 1920x1080 px */
        val previewSize = 1920 to 1080

        /* Retrieve the adjusted frame */
        val rect = omgQRScannerPresenter.adjustFrameInPreview(
                scannerSize.first,
                scannerSize.second,
                framingRect,
                previewSize.first,
                previewSize.second
        )

        /**
         * We put the frame at position (100,100) to (356,356) in the 1920x1080 space
         * When move to the 1600x1200 space, we need to adjust the frame to position (90, 90) to (320, 320)
         */
        rect shouldEqual Rect(90, 90, 320, 320)
    }
}