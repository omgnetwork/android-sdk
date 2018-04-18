package co.omisego.omisego.qrcode

import android.content.Context
import android.hardware.Camera
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import co.omisego.omisego.R
import co.omisego.omisego.custom.camera.CameraWrapper
import co.omisego.omisego.custom.camera.ui.CameraPreview
import co.omisego.omisego.custom.camera.utils.DisplayUtils
import co.omisego.omisego.custom.zxing.CameraHandlerThread
import co.omisego.omisego.custom.zxing.ui.decorator.OMGScannerUI

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
@Suppress("DEPRECATION")
class OMGQRScannerView : FrameLayout, OMGQRScannerContract.View {
    private var mCameraWrapper: CameraWrapper? = null
    private var mCameraHandlerThread: CameraHandlerThread? = null
    private var mLoadingView: View? = null

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

    override var cameraPreview: CameraPreview? = null
    private var mOMGScannerPresenter: OMGQRScannerContract.Presenter? = null
    override val omgScannerUI by lazy {
        OMGScannerUI(context).apply { borderColor = this@OMGQRScannerView.mBorderColor }
    }
    override var orientation: Int = 0
        get() = DisplayUtils.getScreenOrientation(context)

    override var isLoading: Boolean = false
        set(value) {
            when (value) {
                true -> {
                    mLoadingView?.visibility = View.VISIBLE
                    omgScannerUI.hintText = OMGScannerUI.HINT_TEXT_LOADING
                    omgScannerUI.borderColor = mBorderColorLoading
                }
                else -> {
                    mLoadingView?.visibility = View.GONE
                    omgScannerUI.hintText = OMGScannerUI.HINT_TEXT_DEFAULT
                    omgScannerUI.borderColor = mBorderColor
                }
            }
            field = value
        }

    // For Debugging purpose
    /* To debug the QRCode frame is streaming the image data from the camera correctly */
    override var debugging = false

    /* Represents the image data within the QRCode frame */
    override var debugImageView: ImageView? = null

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
        omgScannerUI.setupUI()
        setupLayout(mCameraWrapper)

        this.setOnClickListener {
            // Reset loading when tap on any view
            isLoading = false
            mOMGScannerPresenter?.stopVerifyQR()
        }
    }

    private fun setupLayout(cameraWrapper: CameraWrapper?) {
        removeAllViews()
        cameraPreview = CameraPreview(context, cameraWrapper, this)

        /* Prepare the loading view for display decoding the QR image */
        mLoadingView = ProgressBar(context).apply { visibility = View.GONE }

        /* Add camera preview surface UI */
        addView(cameraPreview)

        /* Add scanner UI */
        addView(omgScannerUI)

        if (debugging) {
            debugImageView = ImageView(context).apply { this.layoutParams = FrameLayout.LayoutParams(200, 200) }
            addView(debugImageView)
        }

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
        cameraPreview?.stopCameraPreview()
        mCameraWrapper?.camera?.release()
        mCameraHandlerThread?.quit()
        mCameraHandlerThread = null
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

    override fun setQRScannerPresenter(presenter: OMGQRScannerContract.Presenter) {
        mOMGScannerPresenter = presenter
    }

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        mOMGScannerPresenter?.onPreviewFrame(data, camera)
    }
}
