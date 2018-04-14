package co.omisego.omisego.custom.zxing.ui.core

import android.graphics.Rect
import android.support.annotation.ColorRes
import co.omisego.omisego.qrcode.OMGQRScannerView


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

        /**
         * Resize the frame to fit in the preview frame correctly
         */
        fun adjustFrameInPreview(scannerWidth: Int,
                                 scannerHeight: Int,
                                 scannerRect: Rect?,
                                 previewWidth: Int,
                                 previewHeight: Int): Rect?

        /**
         * Rotate the image based on the orientation of the raw image
         */
        fun adjustRotation(data: ByteArray,
                           portrait: Boolean,
                           size: Pair<Int, Int>,
                           orientation: Int?): ByteArray

        interface Rotation {
            fun rotate(data: ByteArray, width: Int, height: Int, rotationCount: Int): ByteArray
            fun getRotationCount(orientation: Int?): Int
        }
    }

    /**
     * The callback that will receive events from the OMGQRScannerView
     */
    interface Callback {
        /**
         * Called when a QR code was successfuly decoded to a TransactionRequest object
         *
         * @param view The QR scanner view
         * @param payload The transaction request decoded by the scanner
         */
        fun scannerDidDecode(view: OMGQRScannerView, payload: String)

        /**
         * Called when a QR code has been scanned but the scanner was not able to decode it as a TransactionRequest
         *
         * @param view The QR scanner view
         * @param exception The error returned by the scanner
         */
        fun scannerDidFailToDecode(view: OMGQRScannerView, exception: Exception)
    }
}