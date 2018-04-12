package co.omisego.omisego.custom.zxing.ui.decorator

import android.support.v4.content.ContextCompat
import co.omisego.omisego.BuildConfig
import co.omisego.omisego.R
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
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [21])
class OMGScannerUITest {

    private val mOMGScannerUI by lazy { OMGScannerUI(RuntimeEnvironment.application) }

    @Test
    fun `OMGScanner should set the border color correctly`() {
        mOMGScannerUI.borderColor = R.color.omg_scanner_ui_border
        mOMGScannerUI.borderColor shouldEqualTo ContextCompat.getColor(RuntimeEnvironment.application, R.color.omg_scanner_ui_border)
    }
}