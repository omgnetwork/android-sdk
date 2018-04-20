package co.omisego.omisego.qrcode.scanner

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.graphics.Rect
import android.hardware.Camera
import android.os.HandlerThread
import android.widget.ImageView
import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.custom.camera.CameraWrapper
import co.omisego.omisego.custom.camera.ui.CameraPreview
import co.omisego.omisego.custom.retrofit2.adapter.OMGCall
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.qrcode.scanner.ui.OMGScannerUI

interface OMGQRScannerContract {
    interface View : Camera.PreviewCallback {
        /**
         * Start stream the camera preview
         */
        fun startCamera()

        /**
         * Stop the camera to stream the image preview
         */
        fun stopCamera()

        /* Read write zone */

        /**
         * Set the default color of QR frame border
         */
        var borderColor: Int

        /**
         * Set the color of QR frame border when validating the QR code with the backend side
         */
        var borderColorLoading: Int

        /**
         * Set the [HandlerThread] responsible for control the thread for [onPreviewFrame]
         */
        var cameraHandlerThread: OMGQRScannerView.CameraHandlerThread?

        /**
         * A view that handle the preview image that streaming from the camera
         */
        var cameraPreview: CameraPreview?

        /**
         * A wrapper for [Camera] and the cameraId [Int]
         */
        var cameraWrapper: CameraWrapper?

        /**
         * A debugging flag to see how the QR code processor actually see the preview image from the camera.
         */
        var debugging: Boolean

        /**
         * A debugging image view for display the preview image from the camera
         */
        var debugImageView: ImageView?

        /**
         * Set a loading view for display when validating the QR code with the backend side
         */
        var loadingView: android.view.View?

        /**
         * Set the [OMGQRScannerContract.Presenter] class to handle the logic when  (Optional)
         */
        var omgScannerPresenter: Presenter?

        /**
         * A flag to indicate that the QR code is currently processing or not
         */
        var isLoading: Boolean

        /* Read only zone*/

        /**
         * A [View] for drawing the QR code frame, mask, and the hint text
         */
        val omgScannerUI: OMGScannerUI

        /**
         * An orientation of the device
         */
        val orientation: Int
    }

    interface Presenter {
        /**
         * Resize the frame to fit in the preview frame correctly
         *
         * @param cameraPreviewSize The width and height of the camera preview size
         * @param previewSize The width and height of the preview layout
         * @param qrFrame Represents the QR frame position and size
         *
         * @return The adjusted [Rect] with the correct ratio to the camera preview resolution
         */
        fun adjustFrameInPreview(cameraPreviewSize: Pair<Int, Int>, previewSize: Pair<Int, Int>, qrFrame: Rect?): Rect?

        /**
         * Rotate the image based on the orientation of the raw image data
         *
         * @param data the raw image data from onPreviewFrame method
         * @param size The size of the image (width to height)
         * @param orientation the orientation of the image that return from the function [Rotation.getRotationCount]
         * @return The correct image data for the current orientation of the device
         */
        fun adjustRotation(data: ByteArray, size: Pair<Int, Int>, orientation: Int?): ByteArray

        /**
         * Handle logic when previewing a frame from the camera
         *
         * @param data the contents of the preview frame in the format defined by ImageFormat,
         * which can be queried with getPreviewFormat(). If setPreviewFormat(int) is never called, the default will be the YCbCr_420_SP (NV21) format.
         * @param camera the Camera service object
         */
        fun onPreviewFrame(data: ByteArray, camera: Camera)

        /**
         * Set the [Callback] for retrieve the QR validation result
         *
         * @param callback the callback for retrieve the result
         */
        fun setScanQRListener(callback: Callback)

        /**
         * Cancel loading that verifying the QR code with the backend
         */
        fun cancelLoading()

        interface QRVerifier {
            /**
             * The [OMGCall<TransactionRequest>] that will be assigned when call [requestTransaction], then will use later for cancel the request.
             */
            var callable: OMGCall<TransactionRequest>?

            /**
             * The callback that will be used to get the result from the QR code verification API
             */
            var callback: OMGCallback<TransactionRequest>?

            /**
             * Make request to the EWallet API to verify if the QR code has a valid transaction id
             *
             * @param txId The transaction id which is created by EWallet backend
             * @param fail A lambda that will be invoked when the verification pass
             * @param success A lambda that will be invoked when the verification fail
             */
            fun requestTransaction(
                txId: String,
                fail: (response: OMGResponse<APIError>) -> Unit,
                success: (response: OMGResponse<TransactionRequest>) -> Unit
            )

            /**
             * Cancel the request to the EWallet API
             */
            fun cancelRequest()
        }

        interface Rotation {
            /**
             * Rotate the image data depends on the device orientation
             *
             * @param data Raw image data from the camera that receiving from [Camera.PreviewCallback.onPreviewFrame]
             * @param width Width of the image
             * @param height Height of the image
             * @param orientation The orientation of the image
             */
            fun rotate(data: ByteArray, width: Int, height: Int, orientation: Int?): ByteArray
        }
    }

    /**
     * The callback that will receive events from the OMGQRScannerView
     */
    interface Callback {

        /**
         * Called when the user tap on the screen. The request to the backend will be cancelled.
         *
         * @param view The QR scanner view
         */
        fun scannerDidCancel(view: OMGQRScannerContract.View)

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
