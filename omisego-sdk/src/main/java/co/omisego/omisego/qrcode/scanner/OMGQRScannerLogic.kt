package co.omisego.omisego.qrcode.scanner

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import co.omisego.omisego.constant.Versions
import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.qrcode.scanner.OMGQRScannerContract.Logic.Rotation
import co.omisego.omisego.qrcode.scanner.utils.QRFrameExtractor
import co.omisego.omisego.qrcode.scanner.utils.Rotater
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.Reader
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import java.io.ByteArrayOutputStream
import java.util.EnumMap

internal class OMGQRScannerLogic(
    private val omgQRScannerView: OMGQRScannerContract.View,
    private val omgQRVerifier: OMGQRScannerContract.Logic.QRVerifier,
    private val rotater: OMGQRScannerContract.Logic.Rotation = Rotater(),
    private val qrReader: Reader = MultiFormatReader().apply {
        setHints(
            EnumMap<DecodeHintType, Any>(DecodeHintType::class.java).apply {
                set(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE))
            }
        )
    }
) : OMGQRScannerContract.Logic {
    private var mQRFrameExtractor: QRFrameExtractor? = null
    private var mPreviewSize: Camera.Size? = null
    private val errorTxNotFound: OMGResponse<APIError> by lazy {
        OMGResponse(
            Versions.EWALLET_API,
            false,
            APIError(
                ErrorCode.TRANSACTION_REQUEST_NOT_FOUND,
                "There is no transaction request corresponding to the provided address"
            )
        )
    }

    override var scanCallback: OMGQRScannerContract.Callback? = null

    /* Keep the QR payload that being sent to the server to prevent spamming */
    override val qrPayloadCache: MutableList<String> = mutableListOf()

    /**
     * Rotate the image based on the orientation of the raw image data
     *
     * @param data the raw image data from onPreviewFrame method
     * @param size The size of the image (width to height)
     * @param orientation the orientation of the image that return from the function [Rotation.getRotationCount]
     * @return The correct image data for the current orientation of the device
     */
    override fun adjustRotation(data: ByteArray, size: Pair<Int, Int>, orientation: Int?): ByteArray {
        /* Return the rotated image data */
        return rotater.rotate(data, size.first, size.second, orientation)
    }

    /**
     * Resize the frame to fit in the preview frame correctly
     *
     * @param cameraPreviewSize The width and height of the camera preview size
     * @param previewSize The width and height of the preview layout
     * @param qrFrame Represents the QR frame position and size
     *
     * @return The adjusted [Rect] with the correct ratio to the camera preview resolution
     */
    override fun adjustFrameInPreview(cameraPreviewSize: Pair<Int, Int>, previewSize: Pair<Int, Int>, qrFrame: Rect?): Rect? {
        if (qrFrame == null) return null

        val (previewWidth, previewHeight) = previewSize
        val (cameraPreviewWidth, cameraPreviewHeight) = cameraPreviewSize

        val ratio = when {
            previewWidth / cameraPreviewWidth < previewHeight / cameraPreviewHeight ->
                previewWidth.toFloat() / cameraPreviewWidth
            else ->
                previewHeight.toFloat() / cameraPreviewHeight
        }

        return Rect(
            (qrFrame.left * ratio).toInt(),
            (qrFrame.top * ratio).toInt(),
            (qrFrame.right * ratio).toInt(),
            (qrFrame.bottom * ratio).toInt()
        )
    }

    /**
     * Handle logic when previewing a frame from the camera
     *
     * @param data the contents of the preview frame in the format defined by ImageFormat,
     * which can be queried with getPreviewFormat(). If setPreviewFormat(int) is never called, the default will be the YCbCr_420_SP (NV21) format.
     * @param camera the Camera service object
     */
    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        /* Don't process anything if currently loading */
        if (omgQRScannerView.isLoading) return

        if (mPreviewSize == null) {
            mPreviewSize = camera.parameters.previewSize ?: return
        }

        /* Check if the camera is in portrait or not */
        val portrait = omgQRScannerView.orientation == Configuration.ORIENTATION_PORTRAIT

        val mutableSize = when (portrait) {
            true -> mPreviewSize!!.height to mPreviewSize!!.width
            else -> mPreviewSize!!.width to mPreviewSize!!.height
        }

        /* Rotate the data to correct the orientation */
        val newData = adjustRotation(
            data,
            mPreviewSize!!.width to mPreviewSize!!.height,
            omgQRScannerView.cameraPreview?.displayOrientation ?: 1
        )

        /* Prepare the bitmap for decoding by exclude the superfluous pixels (pixels outside the frame)*/
        if (mQRFrameExtractor == null) {
            val rect = adjustFrameInPreview(
                omgQRScannerView.omgScannerUI.width to omgQRScannerView.omgScannerUI.height,
                mutableSize.first to mutableSize.second,
                omgQRScannerView.omgScannerUI.mFramingRect
            )
            mQRFrameExtractor = QRFrameExtractor(omgQRScannerView.omgScannerUI, rect)
        }

        /* For streaming the image data inside the QR frame to the debugging image view */
        if (omgQRScannerView.debugging) {
            val img = YuvImage(newData, ImageFormat.NV21, mutableSize.first, mutableSize.second, null)
            val outStream = ByteArrayOutputStream()
            img.compressToJpeg(mQRFrameExtractor?.rect, 50, outStream)
            omgQRScannerView.debugImageView?.setImageBitmap(BitmapFactory.decodeByteArray(outStream.toByteArray(), 0, outStream.toByteArray().size))
        }

        val source = mQRFrameExtractor?.extractPixelsInQRFrame(
            newData, mutableSize.first, mutableSize.second
        ) ?: return

        /* Use the original source to decode */
        var rawResult = qrReader.decodeFirstOtherwiseNull(
            BinaryBitmap(HybridBinarizer(source))
        )

        /* Original source doesn't work, let's try to invert black and white pixels */
        rawResult = rawResult ?: qrReader.decodeFirstOtherwiseNull(
            BinaryBitmap(HybridBinarizer(source.invert()))
        )

        rawResult?.text?.let { txId ->

            /* Return immediately if we've already processed this [txId] and it failed with [ErrorCode.TRANSACTION_REQUEST_NOT_FOUND] */
            if (hasTransactionAlreadyFailed(txId)) {
                scanCallback?.scannerDidFailToDecode(omgQRScannerView, errorTxNotFound)
                return@let
            }

            /* Show loading */
            omgQRScannerView.isLoading = true

            /* Verify transactionId with the eWallet backend */
            omgQRVerifier.requestTransaction(txId, { errorResponse ->

                /* Cache txId if error with [ErrorCode.TRANSACTION_REQUEST_NOT_FOUND] code */
                if (errorResponse.data.code == ErrorCode.TRANSACTION_REQUEST_NOT_FOUND) {
                    qrPayloadCache.add(txId)
                }

                /* Delegate a fail callback */
                scanCallback?.scannerDidFailToDecode(omgQRScannerView, errorResponse)

                /* Hide loading */
                omgQRScannerView.isLoading = false
            }) { successResponse ->

                /* Delegate a success callback */
                scanCallback?.scannerDidDecode(omgQRScannerView, successResponse)

                /* Hide loading */
                omgQRScannerView.isLoading = false
            }
        }
    }

    /**
     * Cancel loading that verifying the QR code with the backend
     */
    override fun cancelLoading() {
        omgQRVerifier.cancelRequest()
        scanCallback?.scannerDidCancel(omgQRScannerView)
    }

    /**
     * Verify if the given [txId] has already failed with code [ErrorCode.TRANSACTION_REQUEST_NOT_FOUND] yet
     *
     * @param txId The transaction id which is created by EWallet backend
     * @return true if the given [txId] has already failed with code [ErrorCode.TRANSACTION_REQUEST_NOT_FOUND], otherwise false.
     */
    override fun hasTransactionAlreadyFailed(txId: String) = qrPayloadCache.contains(txId)

    /**
     * Trying to decode first, if some exception arise, then return null.
     */
    private fun Reader.decodeFirstOtherwiseNull(bitmap: BinaryBitmap): Result? {
        return try {
            this.decode(bitmap)
        } catch (ex: Exception) {
            null
        } finally {
            this.reset()
        }
    }
}
