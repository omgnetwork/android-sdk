package co.omisego.omisego.custom.zxing.ui.core


/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
class RotationManager : OMGQRScannerContract.Presenter.Rotation {
    /* Rotate the bitmap data until we get the correct orientation */
    override fun rotate(data: ByteArray, width: Int, height: Int, rotationCount: Int): ByteArray {
        return when (rotationCount) {
            1 -> rotateCW(data, width, height)
            2 -> rotate180(data, width, height)
            3 -> rotateCCW(data, width, height)
            else -> data
        }
    }

    /**
     * Rotate an image by 90 degrees CW.
     *
     * @param data        the image data, in with the first width * height bytes being the luminance data.
     * @param imageWidth  the width of the image
     * @param imageHeight the height of the image
     * @return the rotated bytes
     */
    private fun rotateCW(data: ByteArray, imageWidth: Int, imageHeight: Int): ByteArray {
        val yuv = ByteArray(imageWidth * imageHeight)
        var i = 0
        for (x in 0 until imageWidth) {
            for (y in imageHeight - 1 downTo 0) {
                yuv[i] = data[y * imageWidth + x]
                i++
            }
        }
        return yuv
    }

    /**
     * Rotate an image by 90 degrees CCW.
     *
     * @param data        the image data, in with the first width * height bytes being the luminance data.
     * @param imageWidth  the width of the image
     * @param imageHeight the height of the image
     * @return the rotated bytes
     */
    private fun rotateCCW(data: ByteArray, imageWidth: Int, imageHeight: Int): ByteArray {
        val n = imageWidth * imageHeight
        val yuv = ByteArray(n)
        var i = n - 1
        for (x in 0 until imageWidth) {
            for (y in imageHeight - 1 downTo 0) {
                yuv[i] = data[y * imageWidth + x]
                i--
            }
        }
        return yuv
    }

    /**
     * Rotate an image by 180 degrees.
     *
     * @param data        the image data, in with the first width * height bytes being the luminance data.
     * @param imageWidth  the width of the image
     * @param imageHeight the height of the image
     * @return the rotated bytes
     */
    private fun rotate180(data: ByteArray, imageWidth: Int, imageHeight: Int): ByteArray {
        val n = imageWidth * imageHeight
        val yuv = ByteArray(n)

        var i = n - 1
        for (j in 0 until n) {
            yuv[i] = data[j]
            i--
        }
        return yuv
    }

    override fun getRotationCount(orientation: Int?) = (orientation ?: 90) / 90
}