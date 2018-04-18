package co.omisego.omisego.qrcode

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import co.omisego.omisego.OMGAPIClient
import co.omisego.omisego.R
import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.custom.camera.ui.CameraPreview
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.model.transaction.request.TransactionRequestParams
import co.omisego.omisego.network.ewallet.EWalletClient
import kotlinx.android.synthetic.main.activity_qrscanner.*

class QRScannerActivity : AppCompatActivity(), OMGQRScannerContract.Callback {
    private lateinit var mPreview: CameraPreview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)
        val eWalletClient = EWalletClient.Builder {
            baseUrl = "http://10.5.10.10:4000/api/"
            authenticationToken = "OMGClient MVJDU1E1VHZ1Z2pGemptRFBNTGRhaVhycjE1QUdPTzVOVThGS1djZmFKUTo0b2FEQ0NLNnNuUGwzZm1pdThHWXNtSmtmYzdTSW5TSjRTb2xuaEYxaWJn"
            debug = false
        }.build()

        val omgAPIClient = OMGAPIClient(eWalletClient)

        omgAPIClient?.retrieveTransactionRequest(TransactionRequestParams("ecd86df1-4138-4f17-8146-4eceab535799")).enqueue(object : OMGCallback<TransactionRequest> {
            override fun success(response: OMGResponse<TransactionRequest>) {
                Log.d(this.javaClass.simpleName, response.toString())
            }

            override fun fail(response: OMGResponse<APIError>) {
                Log.d(this.javaClass.simpleName, response.toString())
            }
        })

        val presenter = OMGQRScannerPresenter(scannerView).apply { setScanQRListener(omgAPIClient, this@QRScannerActivity) }
        scannerView.setQRScannerPresenter(presenter)
        scannerView.startCamera()
    }

    override fun scannerDidDecode(view: OMGQRScannerContract.View, payload: OMGResponse<TransactionRequest>) {
//        Log.d("test", "payl")
    }

    override fun scannerDidFailToDecode(view: OMGQRScannerContract.View, exception: OMGResponse<APIError>) {

    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun onResume() {
        super.onResume()
        scannerView.startCamera()
    }
}
