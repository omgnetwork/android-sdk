@file:Suppress("DEPRECATION")

package co.omisego.omisego.qrcode.scanner

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 19/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.OMGAPIClient
import co.omisego.omisego.constant.enums.ErrorCode
import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.custom.retrofit2.adapter.OMGCall
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.TransactionRequest
import co.omisego.omisego.model.params.TransactionRequestParams

class OMGQRVerifier(
    val scannerView: OMGQRScannerContract.View,
    val client: OMGAPIClient,
    val listener: OMGQRVerifierListener
) : OMGQRScannerContract.Preview.Verifier {
    override var postVerification: OMGQRScannerContract.Preview.PostVerification? = null

    /**
     * The [OMGCall<TransactionRequest>] that will be assigned when call [requestTransaction], then will use later for cancel the request.
     */
    var callable: OMGCall<TransactionRequest>? = null

    /**
     * The callback that will be used to get the result from the QR code verification API
     */
    var callback: OMGCallback<TransactionRequest>? = null

    override fun onDecoded(payload: String) {
        callable = client.retrieveTransactionRequest(TransactionRequestParams(payload))
        callback = object : OMGCallback<TransactionRequest> {
            override fun success(response: OMGResponse<TransactionRequest>) {
                postVerification?.onRemoveCache(payload)
                postVerification?.onStopLoading()
                listener.scannerDidDecode(scannerView, response)
            }

            override fun fail(response: OMGResponse<APIError>) {
                /* Cache formattedId if error with [ErrorCode.TRANSACTION_REQUEST_NOT_FOUND] code */
                if (response.data.code != ErrorCode.TRANSACTION_REQUEST_NOT_FOUND) {
                    postVerification?.onRemoveCache(payload)
                }

                postVerification?.onStopLoading()
                listener.scannerDidFailToDecode(scannerView, response)
            }
        }
        callable?.enqueue(callback!!)
    }

    /**
     * Cancel the request to the EWallet API
     */
    override fun onCanceled() {
        callable?.cancel()
        callable = null
        listener.scannerDidCancel(scannerView)
    }
}
