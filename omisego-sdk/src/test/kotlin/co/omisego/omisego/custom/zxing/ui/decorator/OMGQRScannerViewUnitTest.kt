package co.omisego.omisego.custom.zxing.ui.decorator

import android.widget.ProgressBar
import co.omisego.omisego.custom.camera.CameraWrapper
import co.omisego.omisego.custom.camera.ui.CameraPreview
import co.omisego.omisego.qrcode.OMGQRScannerView
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config


/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
@Suppress("DEPRECATION")
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class OMGQRScannerViewUnitTest {
    private val mOMGQRScannerView by lazy { OMGQRScannerView(RuntimeEnvironment.application) }
    private val mCameraWrapper by lazy { CameraWrapper.newInstance() }

    @Test
    fun `When called setupCameraPreview, the child element should be added correctly`() {
        mOMGQRScannerView.setupCameraPreview(mCameraWrapper)
        mOMGQRScannerView.childCount shouldEqualTo 3

        mOMGQRScannerView.getChildAt(0) shouldBeInstanceOf CameraPreview::class
        mOMGQRScannerView.getChildAt(1) shouldBeInstanceOf OMGScannerUI::class
        mOMGQRScannerView.getChildAt(2) shouldBeInstanceOf ProgressBar::class
    }
}