package co.omisego.omisego.qrcode.scanner.ui

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 18/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.graphics.Canvas
import android.graphics.Rect

interface ScannerUI {
    fun drawMask(canvas: Canvas, qrFrame: Rect)
    fun drawQRBorder(canvas: Canvas, qrFrame: Rect)
    fun drawHintText(canvas: Canvas, qrFrame: Rect, hintText: String)

    var borderColor: Int
    var hintText: String
}
