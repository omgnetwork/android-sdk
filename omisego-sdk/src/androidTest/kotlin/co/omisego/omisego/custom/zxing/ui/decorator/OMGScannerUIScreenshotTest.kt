package co.omisego.omisego.custom.zxing.ui.decorator

import android.support.test.InstrumentationRegistry
import android.view.LayoutInflater
import co.omisego.omisego.R
import com.facebook.testing.screenshot.Screenshot
import com.facebook.testing.screenshot.ViewHelpers
import org.junit.Test

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class OMGScannerUIScreenshotTest {
    @Test
    fun doScreenshotTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val view = LayoutInflater.from(context).inflate(R.layout.activity_qrscanner, null, false)

        ViewHelpers.setupView(view)
            .setExactWidthDp(300)
            .layout()

        Screenshot.snap(view)
            .record()
    }
}