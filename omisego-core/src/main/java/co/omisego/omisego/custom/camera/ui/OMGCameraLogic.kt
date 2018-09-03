@file:Suppress("DEPRECATION")

package co.omisego.omisego.custom.camera.ui

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 23/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.graphics.Point
import android.hardware.Camera
import android.view.Surface
import android.view.ViewGroup

class OMGCameraLogic(
    private val cameraPreviewView: CameraPreviewContract.View
) : CameraPreviewContract.Logic {

    /**
     * Retrieve the camera display orientation based on the rotation of the device
     *
     * @param initialized represents the [CameraWrapper] is initialized or not
     * @return The orientation of the camera
     */
    override fun getDisplayOrientation(initialized: Boolean): Int {
        return when (initialized) {
            false -> 0
            else -> {
                val rotation = cameraPreviewView.windowManager.defaultDisplay.rotation
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

    /**
     * Get the optimized camera preview dimension based on the resolution and orientation of the current device
     *
     * @return The optimized size of the camera preview resolution
     */
    override fun getOptimalPreviewSize(): Camera.Size? {

        /* Get all camera available preview sizes except width < 400 (too small) */
        val sizes = cameraPreviewView.supportedPreviewSizes?.filter { it.width > 400 } ?: return null
        var w = cameraPreviewView.previewWidth
        var h = cameraPreviewView.previewHeight

        if (cameraPreviewView.displayOrientation % 180 != 0) {
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

    override fun calculateLayoutParams(dimen: Pair<Int, Int>, pDimen: Pair<Int, Int>): ViewGroup.LayoutParams {
        val (pWidth, pHeight) = pDimen
        val (width, height) = dimen

        val ratioWidth = pWidth.toFloat() / width
        val ratioHeight = pHeight.toFloat() / height

        val compensation = if (ratioWidth > ratioHeight) ratioWidth else ratioHeight

        val newWidth = Math.round(width * compensation)
        val newHeight = Math.round(height * compensation)

        cameraPreviewView.previewLayoutParams.width = newWidth
        cameraPreviewView.previewLayoutParams.height = newHeight

        return cameraPreviewView.previewLayoutParams
    }

    override fun adjustViewSize(cameraSize: Camera.Size) {
        val previewSize = getPointByOrientation(Point(cameraPreviewView.previewWidth, cameraPreviewView.previewHeight))
        val cameraRatio = cameraSize.width.toFloat() / cameraSize.height
        val screenRatio = previewSize.x.toFloat() / previewSize.y

        if (screenRatio > cameraRatio) {
            cameraPreviewView.setViewSize((previewSize.y * cameraRatio).toInt(), previewSize.y)
        } else {
            cameraPreviewView.setViewSize(previewSize.x, (previewSize.x / cameraRatio).toInt())
        }
    }

    override fun getPointByOrientation(size: Point): Point {
        return if (cameraPreviewView.displayOrientation % 180 != 0) {
            Point(size.y, size.x)
        } else {
            size
        }
    }
}
