package co.omisego.omisego.custom.zxing.ui.decorator

import android.graphics.Canvas
import android.graphics.Rect

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 18/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */
interface ScannerUI {
    fun drawMask(canvas: Canvas, framingRect: Rect)
    fun drawQRBorder(canvas: Canvas, framingRect: Rect)
    fun drawHintText(canvas: Canvas, framingRect: Rect, hintText: String)

    var borderColor: Int
    var hintText: String
}