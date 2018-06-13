@file:Suppress("DEPRECATION")

package co.omisego.omisego.custom.camera

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 2/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.hardware.Camera
import android.os.Handler
import java.lang.RuntimeException

internal class AutoFocusManager(private val camera: Camera?, val safe: () -> Boolean) {
    private val mAutoFocusHandler: Handler by lazy { Handler() }
    private var autoFocusCB: Camera.AutoFocusCallback = Camera.AutoFocusCallback { _, _ -> scheduleAutoFocus() }

    fun safeAutoFocus() {
        try {
            camera?.autoFocus(autoFocusCB)
        } catch (re: RuntimeException) {
            scheduleAutoFocus() // wait 1 sec and then do check again
        }
    }

    fun scheduleAutoFocus() {
        mAutoFocusHandler.postDelayed({
            if (safe()) safeAutoFocus()
        }, 1000)
    }

    fun stop() = camera?.cancelAutoFocus()
}
