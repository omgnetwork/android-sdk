@file:Suppress("DEPRECATION")

package co.omisego.omisego.qrcode.scanner

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 19/8/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Rect
import android.hardware.Camera
import co.omisego.omisego.qrcode.scanner.ui.OMGScannerUI
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class OMGQRScannerPreviewDecoderTest {
    /* Mock */
    private val mockOMGCameraPreview: OMGQRScannerContract.View = mock()
    //    private val mockReader: Reader = mock()
    private val mockRotator: OMGQRScannerContract.Preview.Rotation = mock()

    /* Test data */
    private val byteArray by lazy { ByteArray(518400) }
    private val camera by lazy { Camera.open() }
    private val cameraSize by lazy { camera.Size(720, 480) }
    /* A square rectangle with size 256px at the (100,100) */
    private val qrFrame by lazy { Rect(100, 100, 356, 356) }
    /* The dimension of the raw image. let's say 1600x1200 px */
    private val scannerSize by lazy { 1600 to 1200 }
    /* The preview size in the layout. let's say 1920x1080 px */
    private val previewSize by lazy { 1920 to 1080 }

    /* Test instance */
    private val decoder: OMGQRScannerPreviewDecoder by lazy {
        OMGQRScannerPreviewDecoder(mockOMGCameraPreview, mockRotator)
    }

    private val sampleByteArray = byteArrayOf(0x00, 0x01, 0x02, 0x03)

    @Before
    fun setup() {
    }

    @Test
    fun `should be call rotator correctly`() {
        decoder.adjustRotation(sampleByteArray, 2 to 2, 0)
        decoder.adjustRotation(sampleByteArray, 2 to 2, 90)
        decoder.adjustRotation(sampleByteArray, 2 to 2, 180)
        decoder.adjustRotation(sampleByteArray, 2 to 2, 270)

        verify(mockRotator).rotate(sampleByteArray, 2, 2, 0)
        verify(mockRotator).rotate(sampleByteArray, 2, 2, 90)
        verify(mockRotator).rotate(sampleByteArray, 2, 2, 180)
        verify(mockRotator).rotate(sampleByteArray, 2, 2, 270)
    }

    @Test
    fun `should be adjusted frame to fit in the preview size correctly`() {
        /* Retrieve the adjusted frame */
        val rect = decoder.adjustFrameInPreview(
            scannerSize.first to scannerSize.second,
            previewSize.first to previewSize.second,
            qrFrame
        )

        /**
         * We put the frame at position (100,100) to (356,356) in the 1920x1080 space
         * When move to the 1600x1200 space, we need to adjust the frame to position (90, 90) to (320, 320)
         */
        rect shouldEqual Rect(90, 90, 320, 320)
    }

    @Test
    fun `should get preview size correctly`() {
        val camera = Camera.open()
        val cameraSize = camera.Size(9, 16)

        /* Portrait */
        whenever(mockOMGCameraPreview.orientation).thenReturn(Configuration.ORIENTATION_PORTRAIT)
        decoder.getPreviewSize(cameraSize) shouldEqual (16 to 9)

        /* Landscape */
        whenever(mockOMGCameraPreview.orientation).thenReturn(Configuration.ORIENTATION_LANDSCAPE)
        decoder.getPreviewSize(cameraSize) shouldEqual (9 to 16)
    }

    @Test
    fun `should invoke qrReader correctly when decode luminance source`() {
        decoder.decodeLuminanceSource(null) shouldBe null
//        verify(mockReader, times(2)).decode(any())
    }

    @Test
    fun `should decode successful`() = runBlocking<Unit> {
        val mockScannerUI = mock<OMGScannerUI>()
        whenever(
            mockRotator.rotate(byteArray, 720, 480, ORIENTATION_PORTRAIT)
        ).thenReturn(byteArray)
        whenever(mockOMGCameraPreview.omgScannerUI).thenReturn(mockScannerUI)
        whenever(mockScannerUI.mFramingRect).thenReturn(qrFrame)
        whenever(mockScannerUI.width).thenReturn(scannerSize.first)
        whenever(mockScannerUI.height).thenReturn(scannerSize.second)

        decoder.decode(byteArray, ORIENTATION_PORTRAIT, cameraSize)
    }
}
