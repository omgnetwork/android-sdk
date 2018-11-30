@file:Suppress("DEPRECATION")

package co.omisego.omisego.qrcode.scanner

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.content.Context
import android.content.res.Resources
import android.hardware.Camera
import android.os.Handler
import android.os.HandlerThread
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import co.omisego.omisego.R
import co.omisego.omisego.custom.camera.CameraWrapper
import co.omisego.omisego.custom.camera.ui.OMGCameraPreview
import co.omisego.omisego.custom.camera.utils.DisplayUtils
import co.omisego.omisego.qrcode.scanner.ui.OMGScannerUI
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * [OMGQRScannerView] is responsible for scanning the QRCode with customizable border color and loading view
 */
class OMGQRScannerView : FrameLayout, OMGQRScannerContract.View {
    /**
     * A [View] for drawing the QR code frame, mask, and the hint text
     */
    override val omgScannerUI by lazy {
        OMGScannerUI(context).apply { borderColor = this@OMGQRScannerView.borderColor }
    }

    /**
     * An orientation of the device
     */
    override val orientation: Int
        get() = DisplayUtils.getScreenOrientation(context)

    /* Read or write zone */

    /**
     * The border of the QRCode frame
     */
    @ColorInt
    override var borderColor: Int = 0
        get() = when (field) {
            0 -> ContextCompat.getColor(context, R.color.omg_scanner_ui_border)
            else -> field
        }
        set(value) {
            field = try {
                ContextCompat.getColor(context, value)
            } catch (e: Resources.NotFoundException) {
                value
            }
        }

    /**
     * The border of the QRCode frame when checking if the QRCode is valid
     */
    @ColorInt
    override var borderColorLoading: Int = 0
        get() = when (field) {
            0 -> ContextCompat.getColor(context, R.color.omg_scanner_ui_border_loading)
            else -> field
        }
        set(value) {
            field = try {
                ContextCompat.getColor(context, value)
            } catch (e: Resources.NotFoundException) {
                value
            }
        }

    /**
     * Set the [HandlerThread] responsible for control the thread for [onPreviewFrame]
     */
    override var cameraHandlerThread: CameraHandlerThread? = null

    /**
     * A wrapper for [Camera] and the cameraId [Int]
     */
    override var cameraWrapper: CameraWrapper? = null

    /**
     * A view that handle the preview image that streaming from the camera
     */
    override var cameraPreview: OMGCameraPreview? = null

    /**
     * A debugging flag to see how the QR code processor actually see the preview image from the camera.
     */
    override var debugging = false

    /**
     * A debugging image view for display the preview image with the QR frame from the camera
     */
    override var debugImageView: ImageView? = null

    /**
     * A loading view for display when validating the QR code with the backend side
     */
    override var loadingView: View? = null

    /**
     * The [OMGQRScannerContract.Preview] class to handle main logic
     */
    internal var omgScannerPreview: OMGQRScannerContract.Preview? = null

    /**
     * A flag to indicate that the QR code is currently processing or not
     */
    override var isLoading: Boolean = false
        set(value) {
            when (value) {
                true -> {
                    loadingView?.visibility = View.VISIBLE
                    omgScannerUI.hintText = OMGScannerUI.HINT_TEXT_LOADING
                    omgScannerUI.borderColor = borderColorLoading
                }
                else -> {
                    loadingView?.visibility = View.GONE
                    omgScannerUI.hintText = OMGScannerUI.HINT_TEXT_DEFAULT
                    omgScannerUI.borderColor = borderColor
                }
            }
            field = value
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.OMGQRScannerView,
            0, 0)

        try {
            borderColor = a.getColor(R.styleable.OMGQRScannerView_borderColor,
                resources.getColor(R.color.omg_scanner_ui_border))
            borderColorLoading = a.getColor(R.styleable.OMGQRScannerView_borderColorLoading,
                resources.getColor(R.color.omg_scanner_ui_border_loading))
        } finally {
            a.recycle()
        }
    }

    internal fun setupCameraPreview(cameraWrapper: CameraWrapper) {
        this.cameraWrapper = cameraWrapper
        omgScannerUI.setupUI()
        setupLayout()

        this.setOnClickListener {
            // Reset loading when tap on any view
            isLoading = false
            omgScannerPreview?.cancelLoading()
        }
    }

    private fun setupLayout() {
        removeAllViews()

        /* Prepare the loading view for display decoding the QR image */
        cameraPreview = OMGCameraPreview(context, cameraWrapper, this)
        loadingView = ProgressBar(context).apply { visibility = View.GONE }

        /* Add camera preview surface UI */
        addView(cameraPreview)

        /* Add scanner UI */
        addView(omgScannerUI)

        if (debugging) {
            debugImageView = ImageView(context).apply { this.layoutParams = FrameLayout.LayoutParams(200, 200) }
            addView(debugImageView)
        }

        /* Add loading bar UI on top with size 100x100 px */
        addView(loadingView, FrameLayout.LayoutParams(100, 100, Gravity.CENTER))
    }

    /**
     * Start stream the camera preview
     */
    override fun startCameraWithVerifier(verifier: OMGQRScannerContract.Preview.Verifier) {
        initCamera()
        omgScannerPreview = omgScannerPreview ?: OMGQRScannerPreview(this, verifier)
    }

    override fun startCamera(listener: SimpleVerifierListener) {
        initCamera()
        omgScannerPreview = omgScannerPreview ?: OMGQRScannerPreview(this, SimpleVerifier(this, listener))
    }

    private fun initCamera() {
        if (cameraHandlerThread == null)
            cameraHandlerThread = CameraHandlerThread(this)
        cameraHandlerThread?.startCamera()
    }

    /**
     * Stop the camera to stream the image preview
     */
    override fun stopCamera(): Deferred<Unit?> {
        omgScannerPreview?.stopCamera()
        cameraPreview?.stopCameraPreview()
        cameraHandlerThread?.quit()
        cameraHandlerThread = null
        // Time consuming!
        return GlobalScope.async(Dispatchers.IO) { cameraWrapper?.camera?.release() }
    }

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        omgScannerPreview?.onPreviewFrame(data, camera)
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return SavedState(superState).apply {
            mBorderColor = borderColor
            mBorderColorLoading = borderColorLoading
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        borderColor = state.mBorderColor
        borderColorLoading = state.mBorderColorLoading
    }

/* Inner class zone */

    /**
     * This class is responsible for control the thread for [onPreviewFrame]
     * Note: https://developer.android.com/reference/android/hardware/Camera.PreviewCallback.html
     * TL;DR The [onPreviewFrame] callback is invoked on the event thread [Camera.open()] was called from
     */
    inner class CameraHandlerThread internal constructor(private val scannerView: OMGQRScannerView) : HandlerThread("CameraHandlerThread") {
        init {
            start()
        }

        fun startCamera() {
            val localHandler = Handler(looper)
            localHandler.post {
                GlobalScope.launch(Dispatchers.Main) {
                    val wrapper = CameraWrapper.newInstance()
                    scannerView.setupCameraPreview(wrapper)
                }
            }
        }
    }

    private inner class SavedState : BaseSavedState {
        var mBorderColor: Int = 0
        var mBorderColorLoading: Int = 0

        constructor(superState: Parcelable) : super(superState)
        constructor(parcel: Parcel) : super(parcel) {
            mBorderColor = parcel.readInt()
            mBorderColorLoading = parcel.readInt()
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeInt(mBorderColor)
            out?.writeInt(mBorderColorLoading)
        }

        val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}
