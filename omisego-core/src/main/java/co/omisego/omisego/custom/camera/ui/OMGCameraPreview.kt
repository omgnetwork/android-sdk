@file:Suppress("DEPRECATION")

package co.omisego.omisego.custom.camera.ui

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 1/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import co.omisego.omisego.custom.camera.AutoFocusManager
import co.omisego.omisego.custom.camera.CameraWrapper
import java.lang.Exception

@SuppressLint("ViewConstructor")
class OMGCameraPreview : SurfaceView, CameraPreviewContract.View {
    private var mPreviewCallback: Camera.PreviewCallback? = null
    private var mPreviewing: Boolean = false
    private var mSafeFocus: Boolean = false
        get() = mSurfaceCreated && mPreviewing
    private var mSurfaceCreated = false
    private lateinit var mFocusManager: AutoFocusManager
    internal lateinit var mOMGCameraLogic: CameraPreviewContract.Logic
    override var cameraWrapper: CameraWrapper? = null
    override val displayOrientation: Int
        get() = mOMGCameraLogic.getDisplayOrientation(cameraWrapper != null)
    override var previewCallback: Camera.PreviewCallback? = null
    override val previewLayoutParams: ViewGroup.LayoutParams
        get() = layoutParams
    override val previewWidth: Int
        get() = width
    override val previewHeight: Int
        get() = height
    override val supportedPreviewSizes: List<Camera.Size>?
        get() = cameraWrapper?.camera?.parameters?.supportedPreviewSizes
    override val windowManager: WindowManager
        get() = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    companion object {
        private const val TAG = "OMGCameraPreview"
    }

    constructor(
        context: Context,
        cameraWrapper: CameraWrapper?,
        previewCallback: Camera.PreviewCallback
    ) : super(context) {
        init(cameraWrapper, previewCallback)
    }

    constructor(
        context: Context,
        attributeSet: AttributeSet,
        cameraWrapper: CameraWrapper?,
        previewCallback: Camera.PreviewCallback
    ) : super(context, attributeSet) {
        init(cameraWrapper, previewCallback)
    }

    private fun init(cameraWrapper: CameraWrapper?, previewCallback: Camera.PreviewCallback) {
        setCamera(cameraWrapper, previewCallback)
        mFocusManager = AutoFocusManager(cameraWrapper?.camera, ::mSafeFocus)
        holder.addCallback(this)
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        mOMGCameraLogic = OMGCameraLogic(this)
    }

    override fun startCameraPreview() {
        try {
            holder.addCallback(this@OMGCameraPreview)
            mPreviewing = true

            setupCameraParameters()

            /* Setup camera display */
            cameraWrapper?.camera?.let {
                it.setPreviewDisplay(holder)
                it.setDisplayOrientation(mOMGCameraLogic.getDisplayOrientation(cameraWrapper != null))
                it.setPreviewCallback(mPreviewCallback)
                postDelayed({
                    try {
                        it.startPreview()
                    } catch (e: Exception) {
                        Log.e("OMGCameraPreview", e.message)
                    }
                }, 200)
            }

            when {
                mSurfaceCreated -> mFocusManager.safeAutoFocus()
                else -> mFocusManager.scheduleAutoFocus()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString(), e)
        }
    }

    override fun stopCameraPreview() {
        try {
            mPreviewing = false
            holder.removeCallback(this)
            mFocusManager.stop()
            cameraWrapper?.camera?.stopPreview()
            cameraWrapper?.camera?.setPreviewCallback(null)
        } catch (e: Exception) {
            Log.d(OMGCameraPreview.TAG, e.toString(), e)
        }
    }

    override fun setCamera(cameraWrapper: CameraWrapper?, previewCallback: Camera.PreviewCallback) {
        this.cameraWrapper = cameraWrapper
        mPreviewCallback = previewCallback
    }

    override fun setupCameraParameters() {
        /* Retrieve optimal preview size for current resolution of the screen */
        val optimalSize = mOMGCameraLogic.getOptimalPreviewSize() ?: return

        /* Retrieve camera parameters setting object */
        val parameters = cameraWrapper?.camera?.parameters

        /* Use the optimal size for the preview size */
        parameters?.setPreviewSize(optimalSize.width, optimalSize.height)

        /* Set new parameters */
        cameraWrapper?.camera?.parameters = parameters

        /* Adjust new view size */
        mOMGCameraLogic.adjustViewSize(optimalSize)
    }

    override fun setViewSize(adjustedWidth: Int, adjustedHeight: Int) {
        val layoutWidth = if (displayOrientation % 180 != 0) adjustedHeight else adjustedWidth
        val layoutHeight = if (displayOrientation % 180 != 0) adjustedWidth else adjustedHeight

        val newLayoutParams = mOMGCameraLogic.calculateLayoutParams(
            layoutWidth to layoutHeight,
            (parent as View).width to (parent as View).height
        )

        layoutParams = newLayoutParams
    }

    /* Override surface callback */
    override fun surfaceChanged(surfaceHolder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        Log.d("OMGCameraPreview", "surfaceChanged")
        surfaceHolder?.surface.let {
            stopCameraPreview()
            startCameraPreview()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        Log.d("OMGCameraPreview", "surfaceDestroyed")
        mSurfaceCreated = false
        stopCameraPreview()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.d("OMGCameraPreview", "surfaceCreated")
        mSurfaceCreated = true
    }
}
