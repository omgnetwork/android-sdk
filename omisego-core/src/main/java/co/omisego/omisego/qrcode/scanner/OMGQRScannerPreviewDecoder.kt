@file:Suppress("DEPRECATION")

package co.omisego.omisego.qrcode.scanner

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 19/8/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.content.res.Configuration
import android.graphics.Rect
import android.hardware.Camera
import co.omisego.omisego.qrcode.scanner.OMGQRScannerContract.Preview.Rotation
import co.omisego.omisego.qrcode.scanner.utils.QRFrameExtractor
import co.omisego.omisego.qrcode.scanner.utils.Rotater
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.Reader
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.EnumMap

class OMGQRScannerPreviewDecoder(
    private val omgQRScannerView: OMGQRScannerContract.View,
    private val rotater: OMGQRScannerContract.Preview.Rotation = Rotater(),
    private val qrReader: Reader = MultiFormatReader().apply {
        setHints(
            EnumMap<DecodeHintType, Any>(DecodeHintType::class.java).apply {
                set(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE))
            }
        )
    }
) : OMGQRScannerContract.Preview.Decoder {
    private var mQRFrameExtractor: QRFrameExtractor? = null

    /**
     * Resize the frame to fit in the preview frame correctly
     *
     * @param cameraPreviewSize The width and height of the camera preview size
     * @param previewSize The width and height of the preview layout
     * @param qrFrame Represents the QR frame position and size
     *
     * @return The adjusted [Rect] with the correct ratio to the camera preview resolution
     */
    fun adjustFrameInPreview(cameraPreviewSize: Pair<Int, Int>, previewSize: Pair<Int, Int>, qrFrame: Rect?): Rect? {
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
     * Trying to decode first, if some exception arise, then return null.
     */
    internal fun Reader.decodeFirstOtherwiseNull(bitmap: BinaryBitmap): Result? {
        return try {
            this.decode(bitmap)
        } catch (ex: Exception) {
            null
        } finally {
            this.reset()
        }
    }

    internal fun getPreviewSize(cameraPreviewSize: Camera.Size): Pair<Int, Int> {
        /* Check if the camera is in portrait or not */
        val portrait = omgQRScannerView.orientation == Configuration.ORIENTATION_PORTRAIT

        return when (portrait) {
            true -> cameraPreviewSize.height to cameraPreviewSize.width
            else -> cameraPreviewSize.width to cameraPreviewSize.height
        }
    }

    internal fun decodeLuminanceSource(source: LuminanceSource?): Result? {
        /* Use the original source to decode */
        val rawResult = qrReader.decodeFirstOtherwiseNull(
            BinaryBitmap(HybridBinarizer(source))
        )

        /* Original source doesn't work, let's try to invert black and white pixels */
        return rawResult ?: qrReader.decodeFirstOtherwiseNull(
            BinaryBitmap(HybridBinarizer(source?.invert()))
        )
    }

    internal fun createLuminanceSource(previewSizePair: Pair<Int, Int>, newData: ByteArray): PlanarYUVLuminanceSource? {
        /* Prepare the bitmap for decoding by exclude the superfluous pixels (pixels outside the frame)*/
        if (mQRFrameExtractor == null) {
            val rect = adjustFrameInPreview(
                omgQRScannerView.omgScannerUI.width to omgQRScannerView.omgScannerUI.height,
                previewSizePair.first to previewSizePair.second,
                omgQRScannerView.omgScannerUI.mFramingRect
            )
            mQRFrameExtractor = QRFrameExtractor(omgQRScannerView.omgScannerUI, rect)
        }

        return mQRFrameExtractor?.extractPixelsInQRFrame(
            newData, previewSizePair.first, previewSizePair.second
        )
    }

    /**
     * Rotate the image based on the orientation of the raw image data
     *
     * @param data the raw image data from onPreviewFrame method
     * @param size The size of the image (width to height)
     * @param orientation the orientation of the image that return from the function [Rotation.getRotationCount]
     * @return The correct image data for the current orientation of the device
     */
    fun adjustRotation(data: ByteArray, size: Pair<Int, Int>, orientation: Int?): ByteArray {
        /* Return the rotated image data */
        return rotater.rotate(data, size.first, size.second, orientation)
    }

    override suspend fun decode(data: ByteArray, orientation: Int?, cameraPreviewSize: Camera.Size): Result? {
        val previewSizePair = GlobalScope.async(Dispatchers.IO) { getPreviewSize(cameraPreviewSize) }

        /* Rotate the data to correct the orientation */
        val newData = GlobalScope.async(Dispatchers.IO) {
            adjustRotation(
                data,
                cameraPreviewSize.width to cameraPreviewSize.height,
                orientation
            )
        }
        val source = createLuminanceSource(previewSizePair.await(), newData.await())

        return decodeLuminanceSource(source)
    }
}
