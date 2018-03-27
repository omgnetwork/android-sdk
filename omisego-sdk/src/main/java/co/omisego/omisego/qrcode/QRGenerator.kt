package co.omisego.omisego.qrcode

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder

/**
 * For creating a QRCode bitmap
 */
class QRGenerator(
    private val writer: MultiFormatWriter = MultiFormatWriter(),
    private val encoder: BarcodeEncoder = BarcodeEncoder()
) {
    companion object {
        /**
         * The default size of QRCode bitmap in pixels [DEFAULT_SIZE x DEFAULT_SIZE]
         */
        const val DEFAULT_SIZE = 512
    }

    /**
     * Generate QRCode bitmap with the default size 512px
     *
     * @param payload the payload to include in the QRCode image
     * @throws WriterException which may occur when encoding a QRCode
     * @return A QRCode bitmap
     */
    fun generate(payload: String, size: Int = DEFAULT_SIZE) = try {
        encoder.createBitmap(writer.encode(payload, BarcodeFormat.QR_CODE, size, size))
    } catch (e: WriterException) {
        throw e
    }
}
