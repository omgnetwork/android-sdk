package co.omisego.omisego.custom.zxing.ui.core

import android.graphics.Rect
import android.support.annotation.ColorRes
import co.omisego.omisego.custom.zxing.ui.OMGQRScannerView


/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
interface OMGQRScannerContract {
    interface View {
        fun startCamera()
        fun stopCamera()
        fun setLoadingView(view: android.view.View)
        fun setColorBorder(@ColorRes color: Int)
        fun setColorBorderLoading(@ColorRes color: Int)
        fun setScanQRListener(callback: Callback)
    }

    interface Presenter {
        fun getFramingRectInPreview(scannerWidth: Int,
                                    scannerHeight: Int,
                                    scannerRect: Rect?,
                                    previewWidth: Int,
                                    previewHeight: Int): Rect?

        fun adjustRotation(data: ByteArray,
                           portrait: Boolean,
                           width: Int,
                           height: Int,
                           orientation: Int?): ByteArray

        interface Rotation {
            fun rotate(data: ByteArray, width: Int, height: Int, rotationCount: Int): ByteArray
            fun getRotationCount(orientation: Int?): Int
        }
    }

    interface Callback {
        fun scannerDidDecode(view: OMGQRScannerView, payload: String)
        fun scannerDidFailToDecode(view: OMGQRScannerView, exception: Exception)
    }
}