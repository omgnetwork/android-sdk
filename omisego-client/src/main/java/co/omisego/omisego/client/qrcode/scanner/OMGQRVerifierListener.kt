package co.omisego.omisego.client.qrcode.scanner

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.qrcode.scanner.OMGQRScannerContract

/**
 * The callback that will receive events from the OMGQRScannerView
 */
interface OMGQRVerifierListener {

    /**
     * Called when the user tap on the screen. The request to the backend will be cancelled.
     *
     * @param view The QR scanner view
     */
    fun scannerDidCancel(view: OMGQRScannerContract.View)

    /**
     * Called when a QR code was successfully decoded to a TransactionRequest object
     *
     * @param view The QR scanner view
     * @param transactionRequest The transaction request decoded by the scanner
     */
    fun scannerDidDecode(view: OMGQRScannerContract.View, transactionRequest: OMGResponse<TransactionRequest>)

    /**
     * Called when a QR code has been scanned but the scanner was not able to decode it as a TransactionRequest
     *
     * @param view The QR scanner view
     * @param exception The error returned by the scanner
     */
    fun scannerDidFailToDecode(view: OMGQRScannerContract.View, exception: OMGResponse<APIError>)
}
