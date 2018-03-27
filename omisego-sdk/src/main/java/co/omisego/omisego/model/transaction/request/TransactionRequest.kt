package co.omisego.omisego.model.transaction.request

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 27/3/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.graphics.Bitmap
import co.omisego.omisego.model.MintedToken
import co.omisego.omisego.qrcode.QRGenerator

/**
 * Represents a transaction request
 */
data class TransactionRequest(
    val id: String,
    val type: TransactionRequestType,
    val mintedToken: MintedToken,
    val amount: Double?,
    val address: String?
) {
    fun generateQRCode(size: Int = QRGenerator.DEFAULT_SIZE): Bitmap = QRGenerator().generate(id, size)
}
