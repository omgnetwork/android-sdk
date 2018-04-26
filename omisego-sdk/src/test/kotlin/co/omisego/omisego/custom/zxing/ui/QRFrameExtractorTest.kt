package co.omisego.omisego.custom.zxing.ui

import android.graphics.Rect
import co.omisego.omisego.qrcode.scanner.ui.OMGScannerUI
import co.omisego.omisego.qrcode.scanner.utils.QRFrameExtractor
import com.google.zxing.PlanarYUVLuminanceSource
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Test

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class QRFrameExtractorTest {
    private val mockOMGScannerUI: OMGScannerUI = mock()
    private val mRect: Rect? = mock()
    private val mQRFrameExtractor: QRFrameExtractor = QRFrameExtractor(mockOMGScannerUI, mRect)

    @Test
    fun `PixelExtractor should return null if the scanner frame is null`() {
        whenever(mockOMGScannerUI.mFramingRect).thenReturn(null)

        val result = mQRFrameExtractor.extractPixelsInQRFrame(byteArrayOf(0x00), 0, 0)

        result shouldBe null
    }

    @Test
    fun `PixelExtractor should return null if the OMGScannerUI's width is 0`() {
        whenever(mockOMGScannerUI.mFramingRect).thenReturn(Rect(0, 0, 0, 0))
        whenever(mockOMGScannerUI.width).thenReturn(0)
        whenever(mockOMGScannerUI.height).thenReturn(1)

        val result = mQRFrameExtractor.extractPixelsInQRFrame(byteArrayOf(0x00), 0, 0)

        result shouldBe null
    }

    @Test
    fun `PixelExtractor should return null if the OMGScannerUI's height is 0`() {
        whenever(mockOMGScannerUI.mFramingRect).thenReturn(Rect(0, 0, 0, 0))
        whenever(mockOMGScannerUI.width).thenReturn(1)
        whenever(mockOMGScannerUI.height).thenReturn(0)

        val result = mQRFrameExtractor.extractPixelsInQRFrame(byteArrayOf(0x00), 0, 0)

        result shouldBe null
    }

    @Test
    fun `PixelExtractor should return null if width is 0`() {
        whenever(mockOMGScannerUI.mFramingRect).thenReturn(Rect(0, 0, 0, 0))
        whenever(mockOMGScannerUI.width).thenReturn(1)
        whenever(mockOMGScannerUI.height).thenReturn(1)

        val result = mQRFrameExtractor.extractPixelsInQRFrame(byteArrayOf(0x00), 0, 1)

        result shouldBe null
    }

    @Test
    fun `PixelExtractor should return null if height is 0`() {
        whenever(mockOMGScannerUI.mFramingRect).thenReturn(Rect(0, 0, 0, 0))
        whenever(mockOMGScannerUI.width).thenReturn(1)
        whenever(mockOMGScannerUI.height).thenReturn(1)

        val result = mQRFrameExtractor.extractPixelsInQRFrame(byteArrayOf(0x00), 1, 0)

        result shouldBe null
    }

    @Test
    fun `PixelExtractor should return null if the rect is null`() {
        val pixelExtractor = QRFrameExtractor(mockOMGScannerUI, null)
        whenever(mockOMGScannerUI.mFramingRect).thenReturn(Rect(0, 0, 0, 0))
        whenever(mockOMGScannerUI.width).thenReturn(1)
        whenever(mockOMGScannerUI.height).thenReturn(1)

        val result = pixelExtractor.extractPixelsInQRFrame(byteArrayOf(0x00), 0, 0)

        result shouldBe null
    }

    @Test
    fun `PixelExtractor should return PlanarYUVLuminanceSource if everything is correct`() {
        val mockFrame: Rect? = mock()
        mockFrame?.left = 64
        mockFrame?.top = 180
        whenever(mockFrame?.width()).thenReturn(352)
        whenever(mockFrame?.height()).thenReturn(360)

        whenever(mockOMGScannerUI.mFramingRect).thenReturn(Rect(0, 0, 0, 0))
        whenever(mockOMGScannerUI.width).thenReturn(1080)
        whenever(mockOMGScannerUI.height).thenReturn(1584)

        val data = ByteArray(518400)
        val pixelExtractor = QRFrameExtractor(mockOMGScannerUI, mockFrame)
        val result = pixelExtractor.extractPixelsInQRFrame(data, 480, 720)

        result.toString() shouldEqual PlanarYUVLuminanceSource(
                data,
                480,
                720,
                64,
                180,
                352,
                360,
                false
        ).toString()
    }
}