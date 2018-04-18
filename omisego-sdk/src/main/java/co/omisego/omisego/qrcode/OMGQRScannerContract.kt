package co.omisego.omisego.qrcode

import android.graphics.Rect
import android.hardware.Camera
import android.support.annotation.ColorRes
import android.widget.ImageView
import co.omisego.omisego.OMGAPIClient
import co.omisego.omisego.custom.camera.ui.CameraPreview
import co.omisego.omisego.custom.zxing.ui.decorator.OMGScannerUI
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.transaction.request.TransactionRequest

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
interface OMGQRScannerContract {
    interface View : Camera.PreviewCallback{
        fun setColorBorder(@ColorRes color: Int)
        fun setColorBorderLoading(@ColorRes color: Int)
        fun setLoadingView(view: android.view.View)
        fun setQRScannerPresenter(presenter: Presenter)
        fun startCamera()
        fun stopCamera()

        var cameraPreview: CameraPreview?
        var debugging: Boolean
        var debugImageView: ImageView?
        var isLoading: Boolean
        val omgScannerUI: OMGScannerUI
        var orientation: Int
    }

    interface Presenter {
        /**
         * Resize the frame to fit in the preview frame correctly
         */
        fun adjustFrameInPreview(
            scannerWidth: Int,
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

        fun onPreviewFrame(data: ByteArray, camera: Camera)

        fun setScanQRListener(client: OMGAPIClient, callback: Callback)

        fun stopVerifyQR()

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
         * Called when a QR code was successfully decoded to a TransactionRequest object
         *
         * @param view The QR scanner view
         * @param payload The transaction request decoded by the scanner
         */
        fun scannerDidDecode(view: OMGQRScannerContract.View, payload: OMGResponse<TransactionRequest>)

        /**
         * Called when a QR code has been scanned but the scanner was not able to decode it as a TransactionRequest
         *
         * @param view The QR scanner view
         * @param exception The error returned by the scanner
         */
        fun scannerDidFailToDecode(view: OMGQRScannerContract.View, exception: OMGResponse<APIError>)
    }
}