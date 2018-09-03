package co.omisego.omisego.qrcode.scanner

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 29/7/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

interface SimpleVerifierListener {
    fun onDecoded(scannerView: OMGQRScannerContract.View, payload: String)
    fun onCanceled(scannerView: OMGQRScannerContract.View)
}
