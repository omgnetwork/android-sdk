@file:Suppress("DEPRECATION")

package co.omisego.omisego.qrcode.scanner

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 19/4/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omisego.OMGAPIClient
import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.custom.retrofit2.adapter.OMGCall
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.model.transaction.request.TransactionRequestParams

@Suppress("OVERRIDE_BY_INLINE")
internal class OMGQRVerifier(
    val omgAPIClient: OMGAPIClient
) : OMGQRScannerContract.Logic.QRVerifier {

    /**
     * The [OMGCall<TransactionRequest>] that will be assigned when call [requestTransaction], then will use later for cancel the request.
     */
    override var callable: OMGCall<TransactionRequest>? = null

    /**
     * The callback that will be used to get the result from the QR code verification API
     */
    override var callback: OMGCallback<TransactionRequest>? = null

    /**
     * Make request to the EWallet API to verify if the QR code has a valid transaction formattedId
     *
     * @param formattedId The transaction formattedId which is created by EWallet backend
     * @param fail A lambda that will be invoked when the verification pass
     * @param success A lambda that will be invoked when the verification fail
     */
    override inline fun requestTransaction(
        formattedId: String,
        crossinline fail: (response: OMGResponse<APIError>) -> Unit,
        crossinline success: (response: OMGResponse<TransactionRequest>) -> Unit
    ) {
        callable = omgAPIClient.retrieveTransactionRequest(TransactionRequestParams(formattedId))
        callback = object : OMGCallback<TransactionRequest> {
            override fun success(response: OMGResponse<TransactionRequest>) = success(response)
            override fun fail(response: OMGResponse<APIError>) = fail(response)
        }
        callable?.enqueue(callback!!)
    }

    /**
     * Cancel the request to the EWallet API
     */
    override fun cancelRequest() {
        callable?.cancel()
        callable = null
    }
}
