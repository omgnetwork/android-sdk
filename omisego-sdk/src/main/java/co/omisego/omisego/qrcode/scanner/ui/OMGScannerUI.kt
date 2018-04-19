package co.omisego.omisego.qrcode.scanner.ui

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 3/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import co.omisego.omisego.R
import co.omisego.omisego.custom.camera.utils.DisplayUtils
import co.omisego.omisego.extension.dp

/**
 * Represents the UI of then QR code scanner
 */
class OMGScannerUI : View, ScannerUI {
    /* Painter for the corner border */
    private val mBorderPaint by lazy {
        Paint().apply {
            color = borderColor
            isAntiAlias = true
            pathEffect = CornerPathEffect(8f)
            strokeWidth = resources.getInteger(R.integer.omg_scanner_border_width).toFloat()
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
        }
    }

    /* Painter for the background mask (the black overlay) */
    private val mMaskPaint by lazy {
        Paint().apply {
            color = ContextCompat.getColor(context, R.color.omg_scanner_ui_mask)
        }
    }

    /* Painter for write the helping text */
    private val mTextPaint by lazy {
        Paint().apply {
            color = ContextCompat.getColor(context, android.R.color.white)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            textSize = 15.dp
        }
    }

    /* Length for the each line of the cornet depends on device DPI value */
    private var borderLineLength: Int = resources.getInteger(R.integer.omg_scanner_border_length)

    /* Showing a hint below the QR border */
    override var hintText: String = HINT_TEXT_DEFAULT

    /* Define color for draw the border */
    @ColorInt
    override var borderColor = 0
        get() = when (field) {
            0 -> ContextCompat.getColor(context, R.color.omg_scanner_ui_border)
            else -> field
        }
        set(value) {
            field = value
            mBorderPaint.color = value
            setupUI()
        }

    /* Rectangle for the QR code frame */
    var mFramingRect: Rect? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    companion object {
        /* Define the ratio of the size of square to the screen */
        private const val DEFAULT_SQUARE_DIMENSION_RATIO = 0.35f

        const val HINT_TEXT_DEFAULT = "Align QR code within the frame to scan"
        const val HINT_TEXT_LOADING = "Tap any area to cancel"
    }

    fun setupUI() {
        updateFramingRect()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val qrFrame = mFramingRect ?: return
        drawMask(canvas, qrFrame)
        drawQRBorder(canvas, qrFrame)
        drawHintText(canvas, qrFrame, hintText)
    }

    /**
     * Draw 4 rectangles black overlay mask to top, left, right, bottom.
     * Basically, draw mask around of the centered rectangle.
     */
    override fun drawMask(canvas: Canvas, qrFrame: Rect) {
        val width = canvas.width
        val height = canvas.height

        /* Top */
        canvas.drawRect(0, 0, width, qrFrame.top, mMaskPaint)

        /* Left */
        canvas.drawRect(0, qrFrame.top, qrFrame.left, qrFrame.bottom + 1, mMaskPaint)

        /* Right */
        canvas.drawRect(qrFrame.right + 1, qrFrame.top, width, qrFrame.bottom + 1, mMaskPaint)

        /* Bottom */
        canvas.drawRect(0, qrFrame.bottom + 1, width, height, mMaskPaint)
    }

    /**
     * Draw the frame for the QR image
     */
    override fun drawQRBorder(canvas: Canvas, qrFrame: Rect) {
        Path().run {
            with(qrFrame) {
                /* Top-left corner */
                moveTo(left, top + borderLineLength)
                lineTo(left, top)
                lineTo(left + borderLineLength, top)
                canvas.drawPath(this@run, mBorderPaint)

                /* Top-right corner */
                moveTo(right, top + borderLineLength)
                lineTo(right, top)
                lineTo(right - borderLineLength, top)
                canvas.drawPath(this@run, mBorderPaint)

                /* Bottom-left corner */
                moveTo(left, bottom - borderLineLength)
                lineTo(left, bottom)
                lineTo(left + borderLineLength, bottom)
                canvas.drawPath(this@run, mBorderPaint)

                /* Bottom-right corner */
                moveTo(right - borderLineLength, bottom)
                lineTo(right, bottom)
                lineTo(right, bottom - borderLineLength)
                canvas.drawPath(this@run, mBorderPaint)
            }
        }
    }

    override fun drawHintText(canvas: Canvas, qrFrame: Rect, hintText: String) {
        canvas.drawText(
            hintText,
            qrFrame.left.toFloat() + qrFrame.width() / 2,
            qrFrame.bottom.toFloat() + 32.dp,
            mTextPaint
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) = updateFramingRect()

    private fun updateFramingRect() {
        val viewResolution = Point(width, height)
        val orientation = DisplayUtils.getScreenOrientation(context)
        val squareWidth: Int
        val squareHeight: Int

        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                squareHeight = (height * DEFAULT_SQUARE_DIMENSION_RATIO).toInt()
                squareWidth = squareHeight
            }
            else -> {
                squareWidth = (width * DEFAULT_SQUARE_DIMENSION_RATIO).toInt()
                squareHeight = squareWidth
            }
        }

        val leftOffset = (viewResolution.x - squareWidth) / 2
        val topOffset = (viewResolution.y - squareHeight) / 2

        mFramingRect = Rect(
            leftOffset,
            topOffset,
            leftOffset + squareWidth,
            topOffset + squareHeight
        )
    }

    /* Private extension */
    private fun Canvas.drawRect(left: Int, top: Int, right: Int, bottom: Int, paint: Paint) =
        this.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)

    private fun Path.moveTo(x: Int, y: Int) = this.moveTo(x.toFloat(), y.toFloat())
    private fun Path.lineTo(x: Int, y: Int) = this.lineTo(x.toFloat(), y.toFloat())
}
