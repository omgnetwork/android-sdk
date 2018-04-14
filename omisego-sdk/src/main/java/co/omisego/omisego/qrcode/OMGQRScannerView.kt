package co.omisego.omisego.qrcode

import android.content.Context
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.YuvImage
import android.hardware.Camera
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import co.omisego.omisego.R
import co.omisego.omisego.custom.camera.CameraWrapper
import co.omisego.omisego.custom.camera.ui.CameraPreview
import co.omisego.omisego.custom.camera.utils.DisplayUtils
import co.omisego.omisego.custom.zxing.CameraHandlerThread
import co.omisego.omisego.custom.zxing.ui.core.LuminanceSourceGenerator
import co.omisego.omisego.custom.zxing.ui.core.OMGQRScannerContract
import co.omisego.omisego.custom.zxing.ui.core.OMGQRScannerPresenter
import co.omisego.omisego.custom.zxing.ui.decorator.OMGScannerUI
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.io.ByteArrayOutputStream
import java.util.*


/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
@Suppress("DEPRECATION")
class OMGQRScannerView : FrameLayout, Camera.PreviewCallback, OMGQRScannerContract.View {
    private var mCameraWrapper: CameraWrapper? = null
    private var mCameraHandlerThread: CameraHandlerThread? = null
    private var mPreview: CameraPreview? = null
    private var mLoadingView: View? = null
    private var mScanCallback: OMGQRScannerContract.Callback? = null
    private var mLuminanceSourceGenerator: LuminanceSourceGenerator? = null
    private var mOMGScannerPresenter: OMGQRScannerContract.Presenter = OMGQRScannerPresenter()
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private val mOMGScannerUI by lazy {
        OMGScannerUI(context).apply { borderColor = this@OMGQRScannerView.mBorderColor }
    }
    private val mMultiFormatReader: MultiFormatReader by lazy {
        val hints = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java).apply {
            set(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE))
        }
        MultiFormatReader().apply { setHints(hints) }
    }
    private var mIsLoading: Boolean = false
        set(value) {
            when (value) {
                true -> {
                    mLoadingView?.visibility = View.VISIBLE
                    mOMGScannerUI.hintText = OMGScannerUI.HINT_TEXT_LOADING
                    mOMGScannerUI.borderColor = mBorderColorLoading
                }
                else -> {
                    mLoadingView?.visibility = View.GONE
                    mOMGScannerUI.hintText = OMGScannerUI.HINT_TEXT_DEFAULT
                    mOMGScannerUI.borderColor = mBorderColor
                }
            }
            field = value
        }

    /**
     * Set the border of the QRCode frame
     */
    @ColorInt
    private var mBorderColor: Int = 0
        get() = when (field) {
            0 -> ContextCompat.getColor(context, R.color.omg_scanner_ui_border)
            else -> field
        }

    /**
     * Set the border of the QRCode frame when checking if the QRCode is valid
     */
    @ColorInt
    private var mBorderColorLoading: Int = 0
        get() = when (field) {
            0 -> ContextCompat.getColor(context, R.color.omg_scanner_ui_border_loading)
            else -> field
        }

    // For Debugging purpose
    /* To debug the QRCode frame is streaming the image data from the camera correctly */
    private val mDebugging = false

    /* Represents the image data within the QRCode frame */
    private lateinit var debugImageView: ImageView

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

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

    companion object {
        private const val TAG = "OMGQRScannerView"
    }

    fun setupCameraPreview(cameraWrapper: CameraWrapper) {
        mCameraWrapper = cameraWrapper
        mOMGScannerUI.setupUI()
        setupLayout(mCameraWrapper)

        this.setOnClickListener {
            // Reset loading when tap on any view
            mIsLoading = false
            mHandler.removeCallbacks(mRunnable)
        }
    }

    private fun setupLayout(cameraWrapper: CameraWrapper?) {
        removeAllViews()
        mPreview = CameraPreview(context, cameraWrapper, this)

        /* Prepare the loading view for display decoding the QR image */
        mLoadingView = ProgressBar(context).apply { visibility = View.GONE }
        debugImageView = ImageView(context).apply { this.layoutParams = FrameLayout.LayoutParams(200, 200) }

        /* Add camera preview surface UI */
        addView(mPreview)

        /* Add scanner UI */
        addView(mOMGScannerUI)

        if (mDebugging)
            addView(debugImageView)

        /* Add loading bar UI on top with size 100x100 px */
        addView(mLoadingView, FrameLayout.LayoutParams(100, 100, Gravity.CENTER))
    }

    /**
     * Start stream the camera preview
     */
    override fun startCamera() {
        if (mCameraHandlerThread == null)
            mCameraHandlerThread = CameraHandlerThread(this)
        mCameraHandlerThread?.startCamera()
    }


    /**
     * Stop the camera to stream the image preview
     */
    override fun stopCamera() {
        mPreview?.stopCameraPreview()
        mCameraWrapper?.camera?.release()
        mCameraHandlerThread?.quit()
        mCameraHandlerThread = null
    }

    /**
     * Set the QRCode callback
     * See [OMGQRScannerContract.Callback]
     */
    override fun setScanQRListener(callback: OMGQRScannerContract.Callback) {
        mScanCallback = callback
    }

    override fun setLoadingView(view: View) {
        mLoadingView = view
    }

    override fun setColorBorderLoading(@ColorRes color: Int) {
        mBorderColorLoading = ContextCompat.getColor(context, color)
    }

    override fun setColorBorder(@ColorRes color: Int) {
        mBorderColor = ContextCompat.getColor(context, color)
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
        val newData = mOMGScannerPresenter.adjustRotation(
                data,
                portrait,
                size.width to size.height,
                mPreview?.mDisplayOrientation ?: 1
        )

        /* Prepare the bitmap for decoding by exclude the superfluous pixels (pixels outside the frame)*/
        if (mLuminanceSourceGenerator == null) {
            val rect = mOMGScannerPresenter.adjustFrameInPreview(
                    mOMGScannerUI.width,
                    mOMGScannerUI.height,
                    mOMGScannerUI.mFramingRect,
                    mutableSize.first,
                    mutableSize.second
            )
            mLuminanceSourceGenerator = LuminanceSourceGenerator(mOMGScannerUI, rect)
        }

        if (mDebugging) {
            val img = YuvImage(newData, ImageFormat.NV21, mutableSize.first, mutableSize.second, null)
            val baos = ByteArrayOutputStream()
            img.compressToJpeg(mLuminanceSourceGenerator?.rect, 50, baos)
            debugImageView.setImageBitmap(BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().size))
        }

        val source = mLuminanceSourceGenerator?.extractPixelsInFraming(
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
            mHandler = Handler()
            mRunnable = Runnable {
                mIsLoading = false
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                mScanCallback?.scannerDidDecode(this, it)
            }
            mHandler.postDelayed(mRunnable, 2000)
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
