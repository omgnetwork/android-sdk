package co.omisego.omisego.custom.zxing

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import co.omisego.omisego.custom.camera.CameraWrapper
import co.omisego.omisego.qrcode.OMGQRScannerView


/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 2/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class CameraHandlerThread internal constructor(private val scannerView: OMGQRScannerView) : HandlerThread("CameraHandlerThread") {
    init {
        start()
    }

    fun startCamera() {
        val localHandler = Handler(looper)
        localHandler.post {
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post { scannerView.setupCameraPreview(CameraWrapper.newInstance()) }
        }
    }
}
