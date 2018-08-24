@file:Suppress("DEPRECATION")

package co.omisego.omisego.qrcode.scanner

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.hardware.Camera
import android.os.HandlerThread
import android.widget.ImageView
import co.omisego.omisego.custom.camera.CameraWrapper
import co.omisego.omisego.custom.camera.ui.OMGCameraPreview
import co.omisego.omisego.qrcode.scanner.ui.OMGScannerUI
import com.google.zxing.Result
import kotlinx.coroutines.experimental.Deferred

interface OMGQRScannerContract {
    interface View : Camera.PreviewCallback {
        /**
         * Start stream the camera preview with simple verifier that send the raw payload when decoded.
         *
         * @param listener The [SimpleVerifierListener] used for delegate the scan result
         */
        fun startCamera(listener: SimpleVerifierListener)

        /**
         * Start stream the camera preview with custom verifier.
         *
         * @param verifier The custom verifier
         */
        fun startCameraWithVerifier(verifier: Preview.Verifier)

        /**
         * Stop the camera to stream the image preview
         */
        fun stopCamera(): Deferred<Unit?>

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
        var cameraPreview: OMGCameraPreview?

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

    interface Preview {
        /**
         * Keep failed QR payload that being sent to the server to prevent spamming
         */
        val qrPayloadCache: MutableSet<String>

        /**
         * The qr code payload verifier
         */
        val verifier: OMGQRScannerContract.Preview.Verifier?

        /**
         * Handle logic when previewing a frame from the camera
         *
         * @param data the contents of the preview frame in the format defined by ImageFormat,
         * which can be queried with getPreviewFormat(). If setPreviewFormat(int) is never called, the default will be the YCbCr_420_SP (NV21) format.
         * @param camera the Camera service object
         */
        fun onPreviewFrame(data: ByteArray, camera: Camera)

        /**
         * Cancel loading that verifying the QR code with the backend
         */
        fun cancelLoading()

        /**
         * Check whether the given [payload] has already cached.
         *
         * @param payload The payload of the QR code
         * @return true if the given [payload] has already cached, otherwise false.
         */
        fun isCached(payload: String): Boolean

        fun stopCamera()

        interface Verifier {
            var postVerification: PostVerification?

            fun onDecoded(payload: String)
            fun onCanceled()
        }

        interface PostVerification {
            fun onRemoveCache(cacheText: String)
            fun onStopLoading()
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

        interface Decoder {

            /**
             * decode the information coming from onPreviewFrame to Result
             *
             * @param data A byte array of camera preview.
             * @param orientation The orientation of the device.
             * @param cameraPreviewSize The resolution of the preview size.
             *
             * @return A final result including the raw text.
             */
            suspend fun decode(data: ByteArray, orientation: Int?, cameraPreviewSize: Camera.Size): Result?
        }
    }
}
