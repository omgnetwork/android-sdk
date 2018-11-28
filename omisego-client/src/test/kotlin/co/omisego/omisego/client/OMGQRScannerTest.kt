package co.omisego.omisego.client

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 26/11/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.hardware.Camera
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import co.omisego.omisego.client.activity.TestQRScanActivity
import co.omisego.omisego.qrcode.scanner.OMGQRScannerView
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class OMGQRScannerTest {

    @Test
    fun `test OMGCameraPreview initialization and deinitialization`() {
        var scannerView: OMGQRScannerView? = null
        val state = ActivityScenario.launch(TestQRScanActivity::class.java)
        state.moveToState(Lifecycle.State.RESUMED)
        state.onActivity {
            Espresso.onView(ViewMatchers.withId(R.id.scannerView)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            scannerView = it.findViewById(R.id.scannerView)
        }
        state.moveToState(Lifecycle.State.DESTROYED)
        scannerView shouldNotBe null
        scannerView?.cameraHandlerThread shouldEqual null

        /* Check if the camera is released */
        val camera = try {
            Camera.open()
        } catch (e: Exception) {
            null
        }
        camera shouldNotBe null
    }
}
