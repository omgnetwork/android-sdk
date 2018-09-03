@file:Suppress("DEPRECATION")

package co.omisego.omisego.qrcode.scanner

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 19/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

class SimpleVerifier(
    val scannerView: OMGQRScannerContract.View,
    val listener: SimpleVerifierListener
) : OMGQRScannerContract.Preview.Verifier {
    override var postVerification: OMGQRScannerContract.Preview.PostVerification? = null

    override fun onDecoded(payload: String) {
        listener.onDecoded(scannerView, payload)
        postVerification?.onStopLoading()

        // Don't cache anything.
        postVerification?.onRemoveCache(payload)
    }

    override fun onCanceled() {
        postVerification?.onStopLoading()
    }
}
