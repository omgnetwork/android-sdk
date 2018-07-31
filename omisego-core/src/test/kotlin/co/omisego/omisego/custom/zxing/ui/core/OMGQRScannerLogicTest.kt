package co.omisego.omisego.custom.zxing.ui.core

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.content.res.Configuration
import android.graphics.Rect
import co.omisego.omisego.custom.camera.utils.CameraUtils
import co.omisego.omisego.qrcode.scanner.OMGQRScannerContract
import co.omisego.omisego.qrcode.scanner.OMGQRScannerLogic
import com.google.zxing.BarcodeFormat
import com.google.zxing.Reader
import com.google.zxing.Result
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class OMGQRScannerLogicTest {
    private val mockScannerView: OMGQRScannerContract.View = mock()
    private val multiFormatReader: Reader = mock()
    private val mockVerifier: OMGQRScannerContract.Logic.Verifier = mock()
    private val sampleByteArray = byteArrayOf(0x00, 0x01, 0x02, 0x03)
    private val omgQRScannerLogic: OMGQRScannerLogic by lazy {
        OMGQRScannerLogic(mockScannerView, mockVerifier, qrReader = multiFormatReader)
    }

    @Test
    fun `should be adjusted the image rotation data correctly`() {
        val result0 = omgQRScannerLogic.adjustRotation(sampleByteArray, 2 to 2, 0)
        result0 shouldEqual byteArrayOf(0x00, 0x01, 0x02, 0x03)

        val result90 = omgQRScannerLogic.adjustRotation(sampleByteArray, 2 to 2, 90)
        result90 shouldEqual byteArrayOf(0x02, 0x00, 0x03, 0x01)

        val result180 = omgQRScannerLogic.adjustRotation(sampleByteArray, 2 to 2, 180)
        result180 shouldEqual byteArrayOf(0x03, 0x02, 0x01, 0x00)

        val result270 = omgQRScannerLogic.adjustRotation(sampleByteArray, 2 to 2, 270)
        result270 shouldEqual byteArrayOf(0x01, 0x03, 0x00, 0x02)
    }

    @Test
    fun `should be adjusted frame to fit in the preview size correctly`() {
        /* A square rectangle with size 256px at the (100,100) */
        val qrFrame = Rect(100, 100, 356, 356)

        /* The dimension of the raw image. let's say 1600x1200 px */
        val scannerSize = 1600 to 1200

        /* The preview size in the layout. let's say 1920x1080 px */
        val previewSize = 1920 to 1080

        /* Retrieve the adjusted frame */
        val rect = omgQRScannerLogic.adjustFrameInPreview(
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
    fun `should not processed the preview frame if the scanner view is still loading`() {
        whenever(mockScannerView.isLoading).thenReturn(true)

        omgQRScannerLogic.onPreviewFrame(byteArrayOf(),
            CameraUtils.cameraInstance!!.apply {
                parameters.setPreviewSize(480, 720)
            }
        )

        verify(mockVerifier, never()).onDecoded(any())
    }

    @Test
    fun `should invoke the listener onDecoded when the payload is not cached`() {
        mockOnPreviewFrameDeps()
        mockTransactionIdThenPreview("transaction_id_01")

        verify(mockScannerView, times(1)).isLoading = true
        verify(mockVerifier, times(1)).onDecoded(any())
    }

    @Test
    fun `should not invoke the listener onDecoded when the payload is cached`() {
        mockOnPreviewFrameDeps()
        mockTransactionIdThenPreview("transaction_id_01")

        verify(mockScannerView, times(1)).isLoading = true
        verify(mockVerifier, times(1)).onDecoded(any())
    }

    @Test
    fun `should cache the same payload properly`() {
        mockOnPreviewFrameDeps()

        mockTransactionIdThenPreview("transaction_id_01")
        mockTransactionIdThenPreview("transaction_id_02")
        mockTransactionIdThenPreview("transaction_id_01")
        mockTransactionIdThenPreview("transaction_id_01")
        mockTransactionIdThenPreview("transaction_id_01")
        mockTransactionIdThenPreview("transaction_id_03")
        mockTransactionIdThenPreview("transaction_id_03")

        omgQRScannerLogic.qrPayloadCache shouldEqual setOf("transaction_id_01", "transaction_id_02", "transaction_id_03")
        verify(mockVerifier, times(1)).onDecoded("transaction_id_01")
        verify(mockVerifier, times(1)).onDecoded("transaction_id_02")
        verify(mockVerifier, times(1)).onDecoded("transaction_id_03")
    }

    @Test
    fun `should delegate callback correctly when the user tap to cancel loading`() {
        omgQRScannerLogic.cancelLoading()

        verify(mockVerifier, times(1)).onCanceled()
    }

    @Test
    fun `should remove payload from the cache properly`() {
        omgQRScannerLogic.qrPayloadCache.add("cacheText")
        omgQRScannerLogic.qrPayloadCache.add("cacheText2")

        omgQRScannerLogic.onRemoveCache("cacheText")

        omgQRScannerLogic.qrPayloadCache.size shouldEqual 1
        omgQRScannerLogic.qrPayloadCache.contains("cacheText2") shouldEqualTo true
    }

    @Test
    fun `should stop the loading properly`() {
        omgQRScannerLogic.onStopLoading()

        verify(mockScannerView, times(1)).isLoading = false
    }

    @Test
    fun `should call onCanceled of the verifier properly`() {
        omgQRScannerLogic.cancelLoading()

        verify(mockVerifier, times(1)).onCanceled()
    }

    private fun mockOnPreviewFrameDeps() {
        whenever(mockScannerView.isLoading).thenReturn(false)
        whenever(mockScannerView.debugging).thenReturn(false)
        whenever(mockScannerView.orientation).thenReturn(Configuration.ORIENTATION_PORTRAIT)
        whenever(mockScannerView.cameraPreview).thenReturn(mock())
        whenever(mockScannerView.cameraPreview?.displayOrientation).thenReturn(1)
        whenever(mockScannerView.omgScannerUI).thenReturn(mock())
        whenever(mockScannerView.omgScannerUI.width).thenReturn(1920)
        whenever(mockScannerView.omgScannerUI.height).thenReturn(1080)
        whenever(mockScannerView.omgScannerUI.mFramingRect).thenReturn(
            Rect(100, 100, 356, 356)
        )
    }

    private fun mockTransactionIdThenPreview(transactionId: String) {
        whenever(multiFormatReader.decode(any())).thenReturn(
            Result(transactionId, ByteArray(518400), arrayOf(), BarcodeFormat.QR_CODE)
        )

        omgQRScannerLogic.onPreviewFrame(
            ByteArray(518400),
            CameraUtils.cameraInstance!!.apply {
                parameters.setPreviewSize(720, 480)
            }
        )
    }
}
