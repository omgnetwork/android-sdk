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

    override fun adjustRotation(data: ByteArray, portrait: Boolean, width: Int, height: Int, orientation: Int?): ByteArray {
        return when (portrait) {
            true -> {
                /* We need to rotate the data if the orientation is a portrait */
                val rotationCount = rotationManager.getRotationCount(orientation)

                val mutableSize = when (rotationCount) {
                    1, 3 -> height to width
                    else -> width to height
                }

                /* Return the rotated image data */
                rotationManager.rotate(data, mutableSize.first, mutableSize.second, rotationCount)
            }
            else -> {
                /* We don't need to rotate the image data here, so the original is returned */
                data
            }
        }
    }

    override fun getFramingRectInPreview(scannerWidth: Int,
                                         scannerHeight: Int,
                                         scannerRect: Rect?,
                                         previewWidth: Int,
                                         previewHeight: Int): Rect? {

        val rect = Rect(scannerRect)

        /* Adjust the QR Preview width */
        if (previewWidth < scannerWidth) {
            rect.left = rect.left * previewWidth / scannerWidth
            rect.right = rect.right * previewWidth / scannerWidth
        }

        /* Adjust the QR Preview height */
        if (previewHeight < scannerHeight) {
            rect.top = rect.top * previewHeight / scannerHeight
            rect.bottom = rect.bottom * previewHeight / scannerHeight
        }
        return rect
    }
}
