@file:Suppress("DEPRECATION")

package co.omisego.omisego.custom.zxing.ui.decorator

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ProgressBar
import co.omisego.omisego.R
import co.omisego.omisego.custom.camera.CameraWrapper
import co.omisego.omisego.custom.camera.ui.CameraPreviewContract
import co.omisego.omisego.qrcode.scanner.OMGQRScannerView
import co.omisego.omisego.qrcode.scanner.ui.OMGScannerUI
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class OMGQRScannerViewUnitTest {
    private val mOMGQRScannerView by lazy { OMGQRScannerView(RuntimeEnvironment.application) }
    private val mCameraWrapper by lazy { CameraWrapper.newInstance() }

    @Before
    fun setup() {
        mOMGQRScannerView.setupCameraPreview(mCameraWrapper)
    }

    @Test
    fun `OMGQRScannerView should have the correct child element`() {
        mOMGQRScannerView.childCount shouldEqualTo 3
        mOMGQRScannerView.getChildAt(0) shouldBeInstanceOf CameraPreviewContract.View::class
        mOMGQRScannerView.getChildAt(1) shouldBeInstanceOf OMGScannerUI::class
        mOMGQRScannerView.getChildAt(2) shouldBeInstanceOf ProgressBar::class
    }

    @Test
    fun `OMGQRScannerView should have the proper initial state`() {
        mOMGQRScannerView.debugImageView shouldBe null
        mOMGQRScannerView.omgScannerLogic shouldBe null
        mOMGQRScannerView.isLoading shouldEqualTo false
        mOMGQRScannerView.loadingView?.visibility!! shouldEqualTo View.GONE
        mOMGQRScannerView.borderColor shouldEqualTo ContextCompat.getColor(RuntimeEnvironment.application, R.color.omg_scanner_ui_border)
        mOMGQRScannerView.borderColorLoading shouldEqualTo ContextCompat.getColor(RuntimeEnvironment.application, R.color.omg_scanner_ui_border_loading)
    }

    @Test
    fun `OMGQRScannerView should be able to start camera properly`() {
        mOMGQRScannerView.cameraHandlerThread = mock()
        mOMGQRScannerView.startCamera(mock(), mock())

        verify(mOMGQRScannerView.cameraHandlerThread, times(1))?.startCamera()
        mOMGQRScannerView.omgScannerLogic?.scanCallback shouldNotBe null
    }

    @Test
    fun `OMGQRScannerView should be able to stop the camera properly`() {
        mOMGQRScannerView.cameraHandlerThread = mock()
        mOMGQRScannerView.cameraWrapper = CameraWrapper(mock(), 0)
        mOMGQRScannerView.cameraPreview = mock()
        mOMGQRScannerView.stopCamera()

        verify(mOMGQRScannerView.cameraPreview, times(1))?.stopCameraPreview()
        verify(mOMGQRScannerView.cameraWrapper?.camera, times(1))?.release()
        mOMGQRScannerView.cameraHandlerThread shouldBe null
    }
}