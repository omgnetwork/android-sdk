@file:Suppress("DEPRECATION")

package co.omisego.omisego.custom.camera.utils

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.hardware.Camera
import android.util.Log

object CameraUtils {
    private val TAG = "CameraUtils"

    /** A safe way to get an instance of the Camera object.  */
    val cameraInstance: Camera?
        get() = getCameraInstance(defaultCameraId)

    /** Favor back-facing camera by default. If none exists, fallback to whatever camera is available  */
    val defaultCameraId: Int
        get() {
            val numberOfCameras = Camera.getNumberOfCameras()
            val cameraInfo = Camera.CameraInfo()
            var defaultCameraId = -1
            for (i in 0 until numberOfCameras) {
                defaultCameraId = i
                Camera.getCameraInfo(i, cameraInfo)
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    return i
                }
            }

            return defaultCameraId
        }

    /** A safe way to get an instance of the Camera object.  */
    private fun getCameraInstance(cameraId: Int): Camera? {
        var c: Camera? = null
        try {
            c = if (cameraId == -1) {
                Camera.open() // attempt to get a Camera instance
            } else {
                Camera.open(cameraId) // attempt to get a Camera instance
            }
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, e.message, e)
        }

        return c // returns null if camera is unavailable
    }

    fun isFlashSupported(camera: Camera?): Boolean {
        /* Credits: Top answer at http://stackoverflow.com/a/19599365/868173 */
        if (camera != null) {
            val parameters = camera.parameters

            if (parameters.flashMode == null) {
                return false
            }

            val supportedFlashModes = parameters.supportedFlashModes
            if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size == 1 && supportedFlashModes[0] == Camera.Parameters.FLASH_MODE_OFF) {
                return false
            }
        } else {
            return false
        }

        return true
    }
}
