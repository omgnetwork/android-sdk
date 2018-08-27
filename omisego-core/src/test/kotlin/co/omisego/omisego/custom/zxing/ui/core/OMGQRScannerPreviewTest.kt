package co.omisego.omisego.custom.zxing.ui.core

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.content.res.Configuration
import android.graphics.Rect
import co.omisego.omisego.custom.camera.ui.OMGCameraPreview
import co.omisego.omisego.custom.camera.utils.CameraUtils
import co.omisego.omisego.qrcode.scanner.OMGQRScannerContract
import co.omisego.omisego.qrcode.scanner.OMGQRScannerPreview
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.runBlocking
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.coroutines.experimental.EmptyCoroutineContext

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class OMGQRScannerPreviewTest {
    private val mockScannerView: OMGQRScannerContract.View = mock()
    private val mockVerifier: OMGQRScannerContract.Preview.Verifier = mock()
    private val mockOMGCameraPreview: OMGCameraPreview = mock()
    private val mockDecoder: OMGQRScannerContract.Preview.Decoder = mock()
    private val omgQRScannerPreview: OMGQRScannerPreview by lazy {
        spy(OMGQRScannerPreview(mockScannerView, mockVerifier, decoder = mockDecoder))
    }

    @Test
    fun `should not processed the preview frame if the scanner view is still loading`() = runBlocking {
        whenever(mockScannerView.isLoading).thenReturn(true)
        omgQRScannerPreview.onPreviewFrame(byteArrayOf(),
            CameraUtils.cameraInstance!!.apply {
                parameters.setPreviewSize(480, 720)
            }
        )

        verify(mockVerifier, never()).onDecoded(any())
    }

    @Test
    fun `getPreviewOrientation should invoke displayOrientation function of OMGCameraPreview`() {
        val mockCameraPreview: OMGCameraPreview = mock()
        whenever(mockScannerView.cameraPreview).thenReturn(mockCameraPreview)
        whenever(mockScannerView.cameraPreview?.displayOrientation).thenReturn(1)

        runBlocking {
            omgQRScannerPreview.getPreviewOrientation()
            verify(mockCameraPreview, times(1)).displayOrientation
        }
    }

    @Test
    fun `should delegate callback correctly when the user tap to cancel loading`() {
        omgQRScannerPreview.cancelLoading()

        verify(mockVerifier, times(1)).onCanceled()
    }

    @Test
    fun `should remove payload from the cache properly`() {
        omgQRScannerPreview.qrPayloadCache.add("cacheText")
        omgQRScannerPreview.qrPayloadCache.add("cacheText2")

        omgQRScannerPreview.onRemoveCache("cacheText")

        omgQRScannerPreview.qrPayloadCache.size shouldEqual 1
        omgQRScannerPreview.qrPayloadCache.contains("cacheText2") shouldEqualTo true
    }

    @Test
    fun `should stop the loading properly`() {
        omgQRScannerPreview.onStopLoading()
        verify(mockScannerView, times(1)).isLoading = false
    }

    @Test
    fun `should call onCanceled of the verifier properly`() {
        omgQRScannerPreview.cancelLoading()
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

    private fun mockCameraPreview() {
        whenever(mockScannerView.cameraPreview).thenReturn(mockOMGCameraPreview)
    }

    private fun mockPreviewOrientation(orientation: Int) {
        mockCameraPreview()
        whenever(mockOMGCameraPreview.displayOrientation).thenReturn(orientation)
    }
}
