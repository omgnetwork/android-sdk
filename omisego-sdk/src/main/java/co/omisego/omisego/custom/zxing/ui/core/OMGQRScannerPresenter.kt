package co.omisego.omisego.custom.zxing.ui.core

import android.graphics.Rect


/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class OMGQRScannerPresenter(
        private val rotationManager: OMGQRScannerContract.Presenter.Rotation = RotationManager()
) : OMGQRScannerContract.Presenter {

    override fun adjustRotation(data: ByteArray, portrait: Boolean, size: Pair<Int, Int>, orientation: Int?): ByteArray {
        /* We need to rotate the data if the orientation is a portrait */
        val rotationCount = rotationManager.getRotationCount(orientation)

        /* Return the rotated image data */
        return rotationManager.rotate(data, size.first, size.second, rotationCount)
    }

    override fun adjustFrameInPreview(scannerWidth: Int,
                                      scannerHeight: Int,
                                      framingRect: Rect?,
                                      previewWidth: Int,
                                      previewHeight: Int): Rect? {

        if (framingRect == null) return null

        val ratio = when {
            previewWidth / scannerWidth < previewHeight / scannerHeight ->
                previewWidth.toFloat() / scannerWidth
            else ->
                previewHeight.toFloat() / scannerHeight
        }

        return Rect(
                (framingRect.left * ratio).toInt(),
                (framingRect.top * ratio).toInt(),
                (framingRect.right * ratio).toInt(),
                (framingRect.bottom * ratio).toInt()
        )
    }
}
