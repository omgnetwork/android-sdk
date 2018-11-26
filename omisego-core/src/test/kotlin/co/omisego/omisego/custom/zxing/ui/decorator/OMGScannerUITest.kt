package co.omisego.omisego.custom.zxing.ui.decorator

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import co.omisego.omisego.R
import co.omisego.omisego.qrcode.scanner.ui.OMGScannerUI
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class OMGScannerUITest {

    private val mockCanvas: Canvas = mock()
    private val mockRect: Rect = mock()
    private val mOMGScannerUI: OMGScannerUI by lazy { OMGScannerUI(ApplicationProvider.getApplicationContext()) }

    @Test
    fun `OMGScanner should set the border color correctly`() {
        mOMGScannerUI.borderColor = ContextCompat.getColor(ApplicationProvider.getApplicationContext(), R.color.omg_scanner_ui_border)
        mOMGScannerUI.borderColor shouldEqualTo ContextCompat.getColor(ApplicationProvider.getApplicationContext(), R.color.omg_scanner_ui_border)
    }

    @Test
    fun `OMGScanner should set the hint text and invoke correctly`() {
        mOMGScannerUI.hintText shouldEqualTo OMGScannerUI.HINT_TEXT_DEFAULT
        val newHelpingText = "OMG"
        mOMGScannerUI.hintText = newHelpingText
        mOMGScannerUI.hintText shouldEqualTo newHelpingText

        mOMGScannerUI.drawHintText(mockCanvas, mockRect, newHelpingText)

        verify(mockCanvas, times(1)).drawText(eq(newHelpingText), any(), any(), any())
    }

    @Test
    fun `OMGScanner should draw QR border correctly`() {
        mOMGScannerUI.drawQRBorder(mockCanvas, mockRect)

        verify(mockCanvas, times(4)).drawPath(any(), any())
    }

    @Test
    fun `OMGScanner should draw mask correctly`() {
        mOMGScannerUI.drawMask(mockCanvas, mockRect)

        verify(mockCanvas, times(4)).drawRect(any(), any(), any(), any(), any())
    }
}