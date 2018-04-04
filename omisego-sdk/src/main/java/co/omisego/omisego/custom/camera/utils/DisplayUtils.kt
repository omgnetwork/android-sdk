package co.omisego.omisego.custom.camera.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.view.WindowManager


/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
object DisplayUtils {
    fun getScreenOrientation(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val rect = Point()
        display.getSize(rect)

        return when {
            rect.x < rect.y -> Configuration.ORIENTATION_PORTRAIT
            else -> Configuration.ORIENTATION_LANDSCAPE
        }
    }
}
