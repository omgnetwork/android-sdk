package co.omisego.omisego.qrcode.generator

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.graphics.Bitmap
import co.omisego.omisego.model.transaction.request.TransactionRequest
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException

/**
 * For creating a QRCode bitmap
 */
class QRGenerator(
    private val writer: MultiFormatWriter = MultiFormatWriter(),
    private val encoder: QREncoder = QREncoder()
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
    fun generate(payload: String, size: Int = DEFAULT_SIZE) =
        encoder.createBitmap(writer.encode(payload, BarcodeFormat.QR_CODE, size, size))
}

/**
 * Generates an QR bitmap containing the encoded transaction request id
 *
 * @param size the desired image size
 * @return An QR image if the transaction request was successfully encoded
 */
fun TransactionRequest.generateQRCode(size: Int = QRGenerator.DEFAULT_SIZE): Bitmap = QRGenerator().generate(id, size)
