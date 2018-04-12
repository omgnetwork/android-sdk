package co.omisego.omisego.custom.zxing.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.hardware.Camera
import android.os.Handler
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import co.omisego.omisego.R
import co.omisego.omisego.custom.camera.CameraWrapper
import co.omisego.omisego.custom.camera.ui.CameraPreview
import co.omisego.omisego.custom.camera.utils.DisplayUtils
import co.omisego.omisego.custom.zxing.CameraHandlerThread
import co.omisego.omisego.custom.zxing.ui.core.OMGQRScannerContract
import co.omisego.omisego.custom.zxing.ui.core.OMGQRScannerPresenter
import co.omisego.omisego.custom.zxing.ui.decorator.OMGScannerLoadingUI
import co.omisego.omisego.custom.zxing.ui.decorator.OMGScannerUI
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.util.*


/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
@Suppress("DEPRECATION")
class OMGQRScannerView : FrameLayout, Camera.PreviewCallback, OMGQRScannerContract.View {
    private var mBorderColor: Int = R.color.omg_scanner_ui_border
    private var mBorderColorLoading: Int = R.color.omg_scanner_ui_border_loading
    private var mCameraWrapper: CameraWrapper? = null
    private var mCameraHandlerThread: CameraHandlerThread? = null
    private var mPreview: CameraPreview? = null
    private var mLoadingView: View? = null
    private var mIsLoading: Boolean = false
    private var mScanCallback: OMGQRScannerContract.Callback? = null
    private val mOMGScannerUI by lazy {
        OMGScannerUI(context).apply { borderColor = this@OMGQRScannerView.mBorderColor }
    }
    private val mMultiFormatReader: MultiFormatReader by lazy {
        val hints = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java).apply {
            set(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE))
        }
        MultiFormatReader().apply { setHints(hints) }
    }
    private var omgScannerPresenter: OMGQRScannerContract.Presenter = OMGQRScannerPresenter()
    private var pixelExtractor: PixelExtractor? = null

    /* Constructor */
    constructor(context: Context) : super(context)

    @SuppressLint("ResourceAsColor")
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.OMGQRScannerView,
                0, 0)

        try {
            mBorderColor = a.getColor(R.styleable.OMGQRScannerView_borderColor,
                    resources.getColor(R.color.omg_scanner_ui_border))
            mBorderColorLoading = a.getColor(R.styleable.OMGQRScannerView_borderColorLoading,
                    resources.getColor(R.color.omg_scanner_ui_border_loading))
        } finally {
            a.recycle()
        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    companion object {
        private const val TAG = "OMGQRScannerView"
    }

    fun setupCameraPreview(cameraWrapper: CameraWrapper) {
        mCameraWrapper = cameraWrapper
        mOMGScannerUI.setupUI()
        setupLayout(mCameraWrapper)
    }

    private fun setupLayout(cameraWrapper: CameraWrapper?) {
        removeAllViews()
        mPreview = CameraPreview(context, cameraWrapper, this)
        val mLoadingViewLayoutParams = FrameLayout.LayoutParams(100, 100, Gravity.CENTER)
        mLoadingView = OMGScannerLoadingUI(context)

        /* Add camera preview surface UI */
        addView(mPreview)

        /* Add scanner UI */
        addView(mOMGScannerUI)

        /* Add loading bar UI on top */
        addView(mLoadingView, mLoadingViewLayoutParams)
    }

    override fun startCamera() {
        if (mCameraHandlerThread == null)
            mCameraHandlerThread = CameraHandlerThread(this)
        mCameraHandlerThread?.startCamera()
    }

    override fun stopCamera() {
        mPreview?.stopCameraPreview()
        mCameraWrapper?.camera?.release()
        mCameraHandlerThread?.quit()
        mCameraHandlerThread = null
    }

    override fun setScanQRListener(callback: OMGQRScannerContract.Callback) {
        mScanCallback = callback
    }

    override fun setLoadingView(view: View) {
        mLoadingView = view
    }

    override fun setColorBorderLoading(color: Int) {
        mBorderColorLoading = color
    }

    override fun setColorBorder(color: Int) {
        mBorderColor = color
    }

    /**
     * Decode the text from the QR code bitmap.
     */
    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        /* Don't process anything if currently loading */
        if (mIsLoading) return

        /* Try to get preview size */
        val size = try {
            camera.parameters.previewSize
        } catch (ex: RuntimeException) {
            return
        }

        /* Check if the camera is in portrait or not */
        val portrait = DisplayUtils.getScreenOrientation(context) == ORIENTATION_PORTRAIT

        val mutableSize = when (portrait) {
            true -> size.height to size.width
            else -> size.width to size.height
        }

        /* Rotate the data to correct the orientation */
        val newData = omgScannerPresenter.adjustRotation(
                data,
                portrait,
                size.width to size.height,
                mPreview?.mDisplayOrientation ?: 1
        )

        /* Prepare the bitmap for decoding by exclude the superfluous pixels (pixels outside the frame)*/
        if (pixelExtractor == null) {
            val rect = omgScannerPresenter.getFramingRectInPreview(
                    mOMGScannerUI.width,
                    mOMGScannerUI.height,
                    mOMGScannerUI.mFramingRect,
                    mutableSize.first,
                    mutableSize.second
            )
            pixelExtractor = PixelExtractor(mOMGScannerUI, rect)
        }

        val source = pixelExtractor?.extractPixelsInFraming(
                newData, mutableSize.first, mutableSize.second
        ) ?: return

        /* Use the original source to decode */
        var rawResult = mMultiFormatReader.decodeFirstOtherwiseNull(
                BinaryBitmap(HybridBinarizer(source))
        )

        /* Original source doesn't work, let's try to invert black and white pixels */
        rawResult = rawResult ?: mMultiFormatReader.decodeFirstOtherwiseNull(
                BinaryBitmap(HybridBinarizer(source.invert()))
        )

        rawResult?.text?.let {
            mIsLoading = true
            mOMGScannerUI.borderColor = mBorderColorLoading
            mLoadingView?.visibility = View.VISIBLE
            Handler().postDelayed({
                mIsLoading = false
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                mOMGScannerUI.borderColor = mBorderColor
                mLoadingView?.visibility = View.GONE
                mScanCallback?.scannerDidDecode(this, it)
            }, 2000)
        }
    }

    /**
     * Trying to decode first, if some exception was arise, then return null.
     */
    private fun MultiFormatReader.decodeFirstOtherwiseNull(bitmap: BinaryBitmap): Result? {
        return try {
            this.decodeWithState(bitmap)
        } catch (ex: Exception) {
            null
        } finally {
            this.reset()
        }
    }
}
