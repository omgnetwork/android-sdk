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
import android.view.SurfaceHolder
import android.view.ViewGroup
import android.view.WindowManager
import co.omisego.omisego.custom.camera.CameraWrapper

interface CameraPreviewContract {
    interface View : SurfaceHolder.Callback {
        var cameraWrapper: CameraWrapper?
        var previewCallback: Camera.PreviewCallback?
        val supportedPreviewSizes: List<Camera.Size>?
        val previewWidth: Int
        val previewHeight: Int
        val displayOrientation: Int
        val previewLayoutParams: ViewGroup.LayoutParams
        val windowManager: WindowManager

        fun setCamera(cameraWrapper: CameraWrapper?, previewCallback: Camera.PreviewCallback)
        fun setupCameraParameters()
        fun startCameraPreview()
        fun stopCameraPreview()
        fun setViewSize(adjustedWidth: Int, adjustedHeight: Int)
    }

    interface Logic {
        fun getDisplayOrientation(initialized: Boolean): Int
        fun getOptimalPreviewSize(): Camera.Size?
        fun adjustViewSize(cameraSize: Camera.Size)
        fun calculateLayoutParams(dimen: Pair<Int, Int>, pDimen: Pair<Int, Int>): ViewGroup.LayoutParams
        fun getPointByOrientation(size: Point): Point
    }
}
