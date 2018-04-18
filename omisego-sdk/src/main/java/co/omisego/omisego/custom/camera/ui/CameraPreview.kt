package co.omisego.omisego.custom.camera.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.hardware.Camera
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import co.omisego.omisego.custom.camera.AutoFocusManager
import co.omisego.omisego.custom.camera.CameraWrapper
import co.omisego.omisego.custom.camera.utils.DisplayUtils
import java.lang.Exception

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
@Suppress("DEPRECATION")
class CameraPreview : SurfaceView, SurfaceHolder.Callback {
    private var mPreviewCallback: Camera.PreviewCallback? = null
    private var mSurfaceCreated = false
    private var mPreviewing: Boolean = true
    private lateinit var mFocusManager: AutoFocusManager
    private var mCameraWrapper: CameraWrapper? = null
    private var mSafeFocus: Boolean = false
        get() = mSurfaceCreated && mPreviewing
    val mDisplayOrientation: Int
        get() {
            return when (mCameraWrapper) {
                null -> 0
                else -> {
                    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val rotation = wm.defaultDisplay.rotation
                    val degrees = when (rotation) {
                        Surface.ROTATION_90 -> 90
                        Surface.ROTATION_180 -> 180
                        Surface.ROTATION_270 -> 270
                        else -> 0
                    }
                    val info = Camera.CameraInfo()
                    Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info)
                    return (info.orientation - degrees + 360) % 360
                }
            }
        }
    private val mOptimalPreviewSize: Camera.Size?
        get() {
            /* Retrieve all of the support sizes from the camera */
            val sizes = mCameraWrapper?.camera?.parameters?.supportedPreviewSizes ?: return null
            var w = width
            var h = height

            if (DisplayUtils.getScreenOrientation(context) == Configuration.ORIENTATION_PORTRAIT) {
                val tmp = h
                h = w
                w = tmp
            }

            val targetRatio = w.toDouble() / h
            val targetHeight = h

            var optimalSize: Camera.Size? = null
            var minDiff = Double.MAX_VALUE

            for (size in sizes) {
                val ratio = size.width.toDouble() / size.height
                if (Math.abs(ratio - targetRatio) > 0.1f) continue // 0.1f = tolerance
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - targetHeight).toDouble()
                }
            }

            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE
                for (size in sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size
                        minDiff = Math.abs(size.height - targetHeight).toDouble()
                    }
                }
            }
            return optimalSize
        }

    companion object {
        private const val TAG = "CameraPreview"
    }

    constructor(context: Context,
                cameraWrapper: CameraWrapper?,
                previewCallback: Camera.PreviewCallback) : super(context) {
        init(cameraWrapper, previewCallback)
    }

    constructor(context: Context,
                attributeSet: AttributeSet,
                cameraWrapper: CameraWrapper?,
                previewCallback: Camera.PreviewCallback) : super(context, attributeSet) {
        init(cameraWrapper, previewCallback)
    }

    private fun init(cameraWrapper: CameraWrapper?, previewCallback: Camera.PreviewCallback) {
        setCamera(cameraWrapper, previewCallback)
        mFocusManager = AutoFocusManager(cameraWrapper?.camera, ::mSafeFocus)
        holder.addCallback(this)
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    fun stopCameraPreview() {
        try {
            mPreviewing = false
            holder.removeCallback(this)
            mFocusManager.stop()
            mCameraWrapper?.camera?.setPreviewCallback(null)
            mCameraWrapper?.camera?.stopPreview()
        } catch (e: Exception) {
            Log.d(TAG, e.toString(), e)
        }
    }

    fun showCameraPreview() {
        try {
            holder.addCallback(this@CameraPreview)
            mPreviewing = true
            setupCameraParameters()
            mCameraWrapper?.camera?.let {
                it.setPreviewDisplay(holder)
                it.setDisplayOrientation(mDisplayOrientation)
                it.setPreviewCallback(mPreviewCallback)
                it.startPreview()
            }
            when {
                mSurfaceCreated -> mFocusManager.safeAutoFocus()
                else -> mFocusManager.scheduleAutoFocus()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString(), e)
        }
    }

    private fun setCamera(cameraWrapper: CameraWrapper?, previewCallback: Camera.PreviewCallback) {
        mCameraWrapper = cameraWrapper
        mPreviewCallback = previewCallback
    }

    private fun setupCameraParameters() {
        val optimalSize = mOptimalPreviewSize ?: return
        val parameters = mCameraWrapper?.camera?.parameters
        parameters?.setPreviewSize(optimalSize.width, optimalSize.height)
        mCameraWrapper?.camera?.parameters = parameters
        adjustViewSize(optimalSize)
    }

    private fun adjustViewSize(cameraSize: Camera.Size) {
        val previewSize = convertSizeToLandscapeOrientation(Point(width, height))
        val cameraRatio = cameraSize.width.toFloat() / cameraSize.height
        val screenRatio = previewSize.x.toFloat() / previewSize.y

        if (screenRatio > cameraRatio) {
            setViewSize((previewSize.y * cameraRatio).toInt(), previewSize.y)
        } else {
            setViewSize(previewSize.x, (previewSize.x / cameraRatio).toInt())
        }
    }

    private fun convertSizeToLandscapeOrientation(size: Point): Point {
        return if (mDisplayOrientation % 180 == 0) {
            size
        } else {
            Point(size.y, size.x)
        }
    }

    private fun setViewSize(width: Int, height: Int) {
        val layoutParams = layoutParams
        var tmpWidth = if (mDisplayOrientation % 180 == 0) width else height
        var tmpHeight = if (mDisplayOrientation % 180 == 0) height else width

        val parentWidth = (parent as View).width
        val parentHeight = (parent as View).height

        val ratioWidth = parentWidth.toFloat() / tmpWidth.toFloat()
        val ratioHeight = parentHeight.toFloat() / tmpHeight.toFloat()

        val compensation = if (ratioWidth > ratioHeight) ratioWidth else ratioHeight

        tmpWidth = Math.round(tmpWidth * compensation)
        tmpHeight = Math.round(tmpHeight * compensation)

        layoutParams.width = tmpWidth
        layoutParams.height = tmpHeight
        setLayoutParams(layoutParams)
    }

    /* Override surface callback */
    override fun surfaceChanged(surfaceHolder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        surfaceHolder?.surface.let {
            stopCameraPreview()
            showCameraPreview()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mSurfaceCreated = false
        stopCameraPreview()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mSurfaceCreated = true
    }
}
