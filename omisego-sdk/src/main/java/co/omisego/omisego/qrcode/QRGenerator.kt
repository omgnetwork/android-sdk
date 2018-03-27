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

class QRGenerator(
    private val writer: MultiFormatWriter = MultiFormatWriter(),
    private val encoder: BarcodeEncoder = BarcodeEncoder()
) {
    companion object {
        const val DEFAULT_SIZE = 512
    }

    fun generate(transactionId: String, size: Int = DEFAULT_SIZE) = try {
        encoder.createBitmap(writer.encode(transactionId, BarcodeFormat.QR_CODE, size, size))
    } catch (e: WriterException) {
        throw e
    }
}
