package co.omisego.omisego.client.activity

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 26/11/2018 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.omisego.omisego.OMGAPIClient
import co.omisego.omisego.client.R
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.ClientConfiguration
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.TransactionRequest
import co.omisego.omisego.network.ewallet.EWalletClient
import co.omisego.omisego.qrcode.scanner.OMGQRScannerContract
import co.omisego.omisego.qrcode.scanner.OMGQRScannerView
import co.omisego.omisego.qrcode.scanner.OMGQRVerifier
import co.omisego.omisego.qrcode.scanner.OMGQRVerifierListener

class TestQRScanActivity : AppCompatActivity(), OMGQRVerifierListener {
    private lateinit var omgAPIClient: OMGAPIClient
    private lateinit var verifier: OMGQRVerifier
    private lateinit var scannerView: OMGQRScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val eWalletClient = EWalletClient.Builder {
            this.clientConfiguration = ClientConfiguration(
                "http://mock",
                "apiKey",
                null
            )
        }.build()

        omgAPIClient = OMGAPIClient(eWalletClient)
        scannerView = findViewById(R.id.scannerView)
        verifier = OMGQRVerifier(scannerView, omgAPIClient, this)
    }

    override fun scannerDidCancel(view: OMGQRScannerContract.View) {
    }

    override fun scannerDidDecode(view: OMGQRScannerContract.View, transactionRequest: OMGResponse<TransactionRequest>) {
    }

    override fun scannerDidFailToDecode(view: OMGQRScannerContract.View, exception: OMGResponse<APIError>) {
    }

    override fun onStop() {
        super.onStop()
        scannerView.stopCamera()
    }

    override fun onStart() {
        super.onStart()
        scannerView.startCameraWithVerifier(verifier)
    }
}
