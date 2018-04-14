package co.omisego.omisego.custom.zxing.ui

import android.graphics.Rect
import co.omisego.omisego.custom.zxing.ui.core.LuminanceSourceGenerator
import co.omisego.omisego.custom.zxing.ui.decorator.OMGScannerUI
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
class LuminanceSourceGeneratorTest {
    private val mockOMGScannerUI: OMGScannerUI = mock()
    private val mRect: Rect? = mock()
    private val mLuminanceSourceGenerator: LuminanceSourceGenerator = LuminanceSourceGenerator(mockOMGScannerUI, mRect)

    @Test
    fun `PixelExtractor should return null if the scanner frame is null`() {
        whenever(mockOMGScannerUI.mFramingRect).thenReturn(null)

        val result = mLuminanceSourceGenerator.extractPixelsInFraming(byteArrayOf(0x00), 0, 0)

        result shouldBe null
    }

    @Test
    fun `PixelExtractor should return null if the OMGScannerUI's width is 0`() {
        whenever(mockOMGScannerUI.mFramingRect).thenReturn(Rect(0, 0, 0, 0))
        whenever(mockOMGScannerUI.width).thenReturn(0)
        whenever(mockOMGScannerUI.height).thenReturn(1)

        val result = mLuminanceSourceGenerator.extractPixelsInFraming(byteArrayOf(0x00), 0, 0)

        result shouldBe null
    }

    @Test
    fun `PixelExtractor should return null if the OMGScannerUI's height is 0`() {
        whenever(mockOMGScannerUI.mFramingRect).thenReturn(Rect(0, 0, 0, 0))
        whenever(mockOMGScannerUI.width).thenReturn(1)
        whenever(mockOMGScannerUI.height).thenReturn(0)

        val result = mLuminanceSourceGenerator.extractPixelsInFraming(byteArrayOf(0x00), 0, 0)

        result shouldBe null
    }

    @Test
    fun `PixelExtractor should return null if width is 0`() {
        whenever(mockOMGScannerUI.mFramingRect).thenReturn(Rect(0, 0, 0, 0))
        whenever(mockOMGScannerUI.width).thenReturn(1)
        whenever(mockOMGScannerUI.height).thenReturn(1)

        val result = mLuminanceSourceGenerator.extractPixelsInFraming(byteArrayOf(0x00), 0, 1)

        result shouldBe null
    }

    @Test
    fun `PixelExtractor should return null if height is 0`() {
        whenever(mockOMGScannerUI.mFramingRect).thenReturn(Rect(0, 0, 0, 0))
        whenever(mockOMGScannerUI.width).thenReturn(1)
        whenever(mockOMGScannerUI.height).thenReturn(1)

        val result = mLuminanceSourceGenerator.extractPixelsInFraming(byteArrayOf(0x00), 1, 0)

        result shouldBe null
    }

    @Test
    fun `PixelExtractor should return null if the rect is null`() {
        val pixelExtractor = LuminanceSourceGenerator(mockOMGScannerUI, null)
        whenever(mockOMGScannerUI.mFramingRect).thenReturn(Rect(0, 0, 0, 0))
        whenever(mockOMGScannerUI.width).thenReturn(1)
        whenever(mockOMGScannerUI.height).thenReturn(1)

        val result = pixelExtractor.extractPixelsInFraming(byteArrayOf(0x00), 0, 0)

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
        val pixelExtractor = LuminanceSourceGenerator(mockOMGScannerUI, mockFrame)
        val result = pixelExtractor.extractPixelsInFraming(data, 480, 720)

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