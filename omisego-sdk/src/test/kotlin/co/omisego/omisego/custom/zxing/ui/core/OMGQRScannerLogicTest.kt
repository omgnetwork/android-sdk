package co.omisego.omisego.custom.zxing.ui.core

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.content.res.Configuration
import android.graphics.Rect
import co.omisego.omisego.OMGAPIClient
import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.custom.camera.utils.CameraUtils
import co.omisego.omisego.extension.mockEnqueueWithHttpCode
import co.omisego.omisego.helpers.delegation.ResourceFile
import co.omisego.omisego.model.ClientConfiguration
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.network.ewallet.EWalletClient
import co.omisego.omisego.qrcode.scanner.OMGQRScannerContract
import co.omisego.omisego.qrcode.scanner.OMGQRScannerLogic
import co.omisego.omisego.qrcode.scanner.OMGQRVerifier
import com.google.zxing.BarcodeFormat
import com.google.zxing.Reader
import com.google.zxing.Result
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.timeout
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.concurrent.Executor

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class OMGQRScannerLogicTest {
    private val transactionRequestSuccessFile: File by ResourceFile("transaction_request.json")
    private val transactionRequestFailedFile: File by ResourceFile("error-invalid_transaction.json")
    private val omgQRScannerView: OMGQRScannerContract.View = mock()
    private val multiFormatReader: Reader = mock()
    private val sampleByteArray = byteArrayOf(0x00, 0x01, 0x02, 0x03)
    private lateinit var omgQRScannerLogic: OMGQRScannerLogic
    private lateinit var mockWebServer: MockWebServer
    private lateinit var omgAPIClient: OMGAPIClient
    private lateinit var omgQRVerifier: OMGQRVerifier
    private var mockQRPayloadCache: MutableSet<String> = mock()
    private val mockTransactionRequestCb: OMGCallback<TransactionRequest> = mock()

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val mockUrl = mockWebServer.url("/api/client/")

        val config = ClientConfiguration(
            "base_url",
            "apiKey",
            "authToken"
        )

        val eWalletClient = EWalletClient.Builder {
            debugUrl = mockUrl
            clientConfiguration = config
            callbackExecutor = Executor { it.run() }
        }.build()

        omgAPIClient = OMGAPIClient(eWalletClient)
        omgQRVerifier = OMGQRVerifier(omgAPIClient).apply {
            callback = mockTransactionRequestCb
        }
        omgQRScannerLogic = spy(OMGQRScannerLogic(omgQRScannerView, omgQRVerifier, qrReader = multiFormatReader))
        whenever(omgQRScannerLogic.qrPayloadCache).thenReturn(mockQRPayloadCache)
    }

    @Test
    fun `OMGQRScanner should be adjusted the image rotation data correctly`() {
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
    fun `OMGQRScanner should be adjusted frame to fit in the preview size correctly`() {
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
    fun `OMGQRScannerLogic should not processed the preview frame if the scanner view is still loading`() {
        val scanQRCallback = mock<OMGQRScannerContract.Callback>()
        whenever(omgQRScannerView.isLoading).thenReturn(true)
        omgQRScannerLogic.scanCallback = scanQRCallback

        omgQRScannerLogic.onPreviewFrame(byteArrayOf(),
            CameraUtils.cameraInstance!!.apply {
                parameters.setPreviewSize(480, 720)
            }
        )

        verifyZeroInteractions(scanQRCallback)
    }

    @Test
    fun `OMGQRScannerLogic should be invoked the callback scannerDidDecode when the QR image format is correct`() {
        transactionRequestSuccessFile.mockEnqueueWithHttpCode(mockWebServer)
        mockOnPreviewFrameDeps()
        mockTransactionIdThenPreview("transaction_id_01")

        verify(omgQRScannerView, times(1)).isLoading = true
        verify(omgQRScannerLogic.scanCallback, timeout(3000).times(1))?.scannerDidDecode(any(), any())
    }

    @Test
    fun `OMGQRScannerLogic should be invoked the callback scannerDidFailToDecode when the QR image format is incorrect`() {
        transactionRequestFailedFile.mockEnqueueWithHttpCode(mockWebServer)
        mockOnPreviewFrameDeps()
        mockTransactionIdThenPreview("transaction_id_01")

        verify(omgQRScannerView, times(1)).isLoading = true
        verify(omgQRScannerLogic.scanCallback, timeout(3000).times(1))?.scannerDidFailToDecode(any(), any())
    }

    @Test
    fun `OMGQRScannerLogic should cache the transaction formatted_id properly if it is an invalid transaction`() {
        for (i in 0..2) {
            transactionRequestFailedFile.mockEnqueueWithHttpCode(mockWebServer)
        }
        mockOnPreviewFrameDeps()

        mockTransactionIdThenPreview("transaction_id_01")
        mockTransactionIdThenPreview("transaction_id_02")
        verify(mockQRPayloadCache, timeout(3000).times(1)).add("transaction_id_02")

        mockTransactionIdThenPreview("transaction_id_01")
        verify(mockQRPayloadCache, timeout(3000).times(1)).add("transaction_id_01")
    }

    private fun mockOnPreviewFrameDeps() {
        val mockScanQRCallback = mock<OMGQRScannerContract.Callback>()
        whenever(omgQRScannerView.isLoading).thenReturn(false)
        whenever(omgQRScannerView.debugging).thenReturn(false)
        whenever(omgQRScannerView.orientation).thenReturn(Configuration.ORIENTATION_PORTRAIT)
        whenever(omgQRScannerView.cameraPreview).thenReturn(mock())
        whenever(omgQRScannerView.cameraPreview?.displayOrientation).thenReturn(1)
        whenever(omgQRScannerView.omgScannerUI).thenReturn(mock())
        whenever(omgQRScannerView.omgScannerUI.width).thenReturn(1920)
        whenever(omgQRScannerView.omgScannerUI.height).thenReturn(1080)
        whenever(omgQRScannerView.omgScannerUI.mFramingRect).thenReturn(
            Rect(100, 100, 356, 356)
        )

        omgQRScannerLogic.scanCallback = mockScanQRCallback
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

    @Test
    fun `OMGQRScannerLogic should delegate callback correctly when the user tap to cancel loading`() {
        val mockScanQRCallback = mock<OMGQRScannerContract.Callback>()

        omgQRScannerLogic.scanCallback = mockScanQRCallback
        omgQRScannerLogic.cancelLoading()

        verify(mockScanQRCallback, times(1)).scannerDidCancel(omgQRScannerView)
        verifyNoMoreInteractions(mockScanQRCallback)
    }
}
