package co.omisego.omisego.custom.zxing.ui.core

import android.content.res.Configuration
import android.graphics.Rect
import co.omisego.omisego.OMGAPIClient
import co.omisego.omisego.custom.camera.utils.CameraUtils
import co.omisego.omisego.model.transaction.request.TransactionRequestParams
import co.omisego.omisego.qrcode.OMGQRScannerContract
import co.omisego.omisego.qrcode.OMGQRScannerPresenter
import com.google.zxing.BarcodeFormat
import com.google.zxing.Reader
import com.google.zxing.Result
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.any
import org.amshove.kluent.mock
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
    private val omgQRScannerView: OMGQRScannerContract.View = mock()
    private val omgAPIClient: OMGAPIClient = mock()
    private val multiFormatReader: Reader = mock()
    private val omgQRScannerPresenter: OMGQRScannerPresenter = OMGQRScannerPresenter(omgQRScannerView, multiFormatReader = multiFormatReader)
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

    @Test
    fun `OMGQRScannerPresenter should not processed the preview frame if the scanner view is still loading`() {
        val scanQRCallback = mock<OMGQRScannerContract.Callback>()
        whenever(omgQRScannerView.isLoading).thenReturn(true)
        omgQRScannerPresenter.setScanQRListener(omgAPIClient, scanQRCallback)

        omgQRScannerPresenter.onPreviewFrame(byteArrayOf(),
            CameraUtils.cameraInstance!!.apply {
                parameters.setPreviewSize(480, 720)
            }
        )

        verifyZeroInteractions(scanQRCallback)
    }

    @Test
    fun `OMGQRScannerPresenter should be invoked the callback scannerDidDecode successfully`() {
        val scanQRCallback = mock<OMGQRScannerContract.Callback>()
        whenever(omgQRScannerView.isLoading).thenReturn(false)
        whenever(omgQRScannerView.debugging).thenReturn(false)
        whenever(omgQRScannerView.orientation).thenReturn(Configuration.ORIENTATION_PORTRAIT)
        whenever(omgQRScannerView.cameraPreview).thenReturn(mock())
        whenever(omgQRScannerView.cameraPreview?.mDisplayOrientation).thenReturn(1)
        whenever(omgQRScannerView.omgScannerUI).thenReturn(mock())
        whenever(omgQRScannerView.omgScannerUI.width).thenReturn(1920)
        whenever(omgQRScannerView.omgScannerUI.height).thenReturn(1080)
        whenever(omgQRScannerView.omgScannerUI.mFramingRect).thenReturn(
            Rect(100, 100, 356, 356)
        )
        whenever(multiFormatReader.decode(any())).thenReturn(
            Result("OMG", ByteArray(518400), arrayOf(), BarcodeFormat.QR_CODE)
        )

        omgQRScannerPresenter.setScanQRListener(omgAPIClient, scanQRCallback)

        omgQRScannerPresenter.onPreviewFrame(
            ByteArray(518400),
            CameraUtils.cameraInstance!!.apply {
                parameters.setPreviewSize(720, 480)
            }
        )

        verify(omgQRScannerView, times(1)).isLoading = true
        verify(omgAPIClient, times(1)).retrieveTransactionRequest(TransactionRequestParams("OMG"))
    }
}