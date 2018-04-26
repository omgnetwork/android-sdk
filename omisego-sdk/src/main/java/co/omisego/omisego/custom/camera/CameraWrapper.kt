package co.omisego.omisego.custom.camera

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 2/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.hardware.Camera
import co.omisego.omisego.custom.camera.utils.CameraUtils

class CameraWrapper(val camera: Camera?, val cameraId: Int) {
    companion object {
        fun newInstance(): CameraWrapper {
            return CameraWrapper(CameraUtils.cameraInstance, CameraUtils.defaultCameraId)
        }
    }
}
