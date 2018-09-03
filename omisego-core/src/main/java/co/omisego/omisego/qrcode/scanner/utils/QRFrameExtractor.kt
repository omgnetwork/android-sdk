package co.omisego.omisego.qrcode.scanner.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 12/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.graphics.Rect
import co.omisego.omisego.qrcode.scanner.ui.OMGScannerUI
import com.google.zxing.PlanarYUVLuminanceSource

class QRFrameExtractor(private val omgScannerUI: OMGScannerUI, val rect: Rect? = null) {
    fun extractPixelsInQRFrame(data: ByteArray, width: Int, height: Int): PlanarYUVLuminanceSource? {

        /* Return now when something is incorrect */
        with(omgScannerUI) {
            if (this.mFramingRect == null ||
                    this.width == 0 ||
                    this.height == 0 ||
                    rect == null ||
                    width == 0 ||
                    height == 0) return null
        }

        return try {
            PlanarYUVLuminanceSource(
                    data,
                    width,
                    height,
                    rect!!.left,
                    rect.top,
                    rect.width(),
                    rect.height(),
                    false
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
