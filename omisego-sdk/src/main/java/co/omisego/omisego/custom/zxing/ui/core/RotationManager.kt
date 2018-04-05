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
        var mData = data
        var mWidth = width
        var mHeight = height

        when (rotationCount) {
            1, 3 -> {
                for (i in 0 until rotationCount) {
                    val rotatedData = ByteArray(mData.size)
                    for (y in 0 until mHeight) {
                        for (x in 0 until mWidth)
                            rotatedData[x * mHeight + mHeight - y - 1] = mData[x + y * mWidth]
                    }
                    mData = rotatedData
                    val tmp = mWidth
                    mWidth = mHeight
                    mHeight = tmp
                }
            }
        }

        return mData
    }

    override fun getRotationCount(orientation: Int?) = (orientation ?: 90) / 90
}