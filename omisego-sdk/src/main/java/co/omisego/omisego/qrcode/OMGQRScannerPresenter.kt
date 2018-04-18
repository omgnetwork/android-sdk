package co.omisego.omisego.qrcode

import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import android.os.Handler
import android.util.Log
import co.omisego.omisego.OMGAPIClient
import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.custom.zxing.ui.core.LuminanceSourceGenerator
import co.omisego.omisego.custom.zxing.ui.core.RotationManager
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.model.transaction.request.TransactionRequestParams
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.Reader
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import java.io.ByteArrayOutputStream
import java.util.EnumMap

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class OMGQRScannerPresenter(
    private val omgQRScannerView: OMGQRScannerContract.View,
    private val rotationManager: OMGQRScannerContract.Presenter.Rotation = RotationManager(),
    private val multiFormatReader: Reader = MultiFormatReader().apply {
        setHints(
            EnumMap<DecodeHintType, Any>(DecodeHintType::class.java).apply {
                set(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE))
            }
        )
    }
) : OMGQRScannerContract.Presenter {

    private var mHandler: Handler? = null
    private var mLuminanceSourceGenerator: LuminanceSourceGenerator? = null
    private var mPreviewSize: Camera.Size? = null
    private var mOMGAPIClient: OMGAPIClient? = null
    private lateinit var mRunnable: Runnable
    private var mScanCallback: OMGQRScannerContract.Callback? = null

    override fun adjustRotation(data: ByteArray, portrait: Boolean, size: Pair<Int, Int>, orientation: Int?): ByteArray {
        /* We need to rotate the data if the orientation is a portrait */
        val rotationCount = rotationManager.getRotationCount(orientation)

        /* Return the rotated image data */
        return rotationManager.rotate(data, size.first, size.second, rotationCount)
    }

    override fun adjustFrameInPreview(scannerWidth: Int,
        scannerHeight: Int,
        framingRect: Rect?,
        previewWidth: Int,
        previewHeight: Int): Rect? {

        if (framingRect == null) return null

        val ratio = when {
            previewWidth / scannerWidth < previewHeight / scannerHeight ->
                previewWidth.toFloat() / scannerWidth
            else ->
                previewHeight.toFloat() / scannerHeight
        }

        return Rect(
            (framingRect.left * ratio).toInt(),
            (framingRect.top * ratio).toInt(),
            (framingRect.right * ratio).toInt(),
            (framingRect.bottom * ratio).toInt()
        )
    }

    /**
     * Decode the text from the QR code bitmap.
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
            portrait,
            mPreviewSize!!.width to mPreviewSize!!.height,
            omgQRScannerView.cameraPreview?.mDisplayOrientation ?: 1
        )

        /* Prepare the bitmap for decoding by exclude the superfluous pixels (pixels outside the frame)*/
        if (mLuminanceSourceGenerator == null) {
            val rect = adjustFrameInPreview(
                omgQRScannerView.omgScannerUI.width,
                omgQRScannerView.omgScannerUI.height,
                omgQRScannerView.omgScannerUI.mFramingRect,
                mutableSize.first,
                mutableSize.second
            )
            mLuminanceSourceGenerator = LuminanceSourceGenerator(omgQRScannerView.omgScannerUI, rect)
        }

        if (omgQRScannerView.debugging) {
            val img = YuvImage(newData, ImageFormat.NV21, mutableSize.first, mutableSize.second, null)
            val baos = ByteArrayOutputStream()
            img.compressToJpeg(mLuminanceSourceGenerator?.rect, 50, baos)
            omgQRScannerView.debugImageView?.setImageBitmap(BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().size))
        }

        val source = mLuminanceSourceGenerator?.extractPixelsInFraming(
            newData, mutableSize.first, mutableSize.second
        ) ?: return

        /* Use the original source to decode */
        var rawResult = multiFormatReader.decodeFirstOtherwiseNull(
            BinaryBitmap(HybridBinarizer(source))
        )

        /* Original source doesn't work, let's try to invert black and white pixels */
        rawResult = rawResult ?: multiFormatReader.decodeFirstOtherwiseNull(
            BinaryBitmap(HybridBinarizer(source.invert()))
        )

        rawResult?.text?.let {
            omgQRScannerView.isLoading = true
//            mHandler = Handler()
//            mRunnable = Runnable {
//                omgQRScannerView.isLoading = false
                Log.d(this.javaClass.simpleName, it)
//                mScanCallback?.scannerDidDecode(omgQRScannerView, it)
//            }
            mOMGAPIClient?.retrieveTransactionRequest(TransactionRequestParams(it))?.enqueue(object : OMGCallback<TransactionRequest> {
                override fun success(response: OMGResponse<TransactionRequest>) {
                    Log.d(this.javaClass.simpleName, response.toString())
                    mScanCallback?.scannerDidDecode(omgQRScannerView, response)
                    omgQRScannerView.isLoading = false
                }

                override fun fail(response: OMGResponse<APIError>) {
                    Log.d(this.javaClass.simpleName, response.toString())
                    mScanCallback?.scannerDidFailToDecode(omgQRScannerView, response)
                    omgQRScannerView.isLoading = false
                }
            })
//            mHandler?.postDelayed(mRunnable, 2000)
        }
    }

    /**
     * Set the QRCode callback
     * See [OMGQRScannerContract.Callback]
     */
    override fun setScanQRListener(client: OMGAPIClient, callback: OMGQRScannerContract.Callback) {
        mScanCallback = callback
        mOMGAPIClient = client
    }

    override fun stopVerifyQR() {
        mHandler?.removeCallbacks(mRunnable)
    }

    /**
     * Trying to decode first, if some exception was arise, then return null.
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
