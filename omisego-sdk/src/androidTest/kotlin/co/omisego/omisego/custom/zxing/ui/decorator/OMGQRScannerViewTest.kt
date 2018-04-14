package co.omisego.omisego.custom.zxing.ui.decorator

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import co.omisego.omisego.R
import co.omisego.omisego.qrcode.OMGQRScannerView
import co.omisego.omisego.qrcode.QRScannerActivity
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.core.AllOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 5/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class OMGQRScannerViewTest {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(QRScannerActivity::class.java)

    @Before
    fun setup() {
//        activityTestRule.activity
        activityTestRule.activity.setTheme(R.style.Theme_AppCompat)
    }

    @Test
    fun verifyThatOMGQRScannerViewAppearOnTheScreenCorrectly() {
        val parentView = onView(withId(R.id.cameraPreview))
        parentView.check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))

        val scannerView = onView(
                AllOf.allOf(
                        instanceOf(OMGQRScannerView::class.java)
                )
        )

        scannerView.check(matches(isDisplayed()))
        scannerView.check(matches(hasChildCount(3)))
    }
}