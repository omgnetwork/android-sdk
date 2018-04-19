package co.omisego.omisego.qrcode

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.omisego.omisego.OMGAPIClient
import co.omisego.omisego.R
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.transaction.request.TransactionRequest
import co.omisego.omisego.network.ewallet.EWalletClient
import co.omisego.omisego.qrcode.scanner.OMGQRScannerContract
import co.omisego.omisego.qrcode.scanner.OMGQRScannerPresenter
import kotlinx.android.synthetic.main.activity_qrscanner.*

class QRScannerActivity : AppCompatActivity(), OMGQRScannerContract.Callback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)
        val eWalletClient = EWalletClient.Builder {
            baseUrl = "http://192.168.1.100:4000/api/"
            authenticationToken = "NGNuNjBWUk9jck8zWGRIenFJSUFWVDViazhXZ0p1ejZIcno5LVpUWUlzczpueGVzRWVud3AtcktlWm0xVzBkQ05xeU1LMEYzaDVXbnpxUW9UZ0Q1WXg0"
            debug = true
        }.build()

        val omgAPIClient = OMGAPIClient(eWalletClient)

        val presenter = OMGQRScannerPresenter(scannerView).apply { setScanQRListener(omgAPIClient, this@QRScannerActivity) }
        scannerView.omgScannerPresenter = presenter
        scannerView.startCamera()
    }

    override fun scannerDidDecode(view: OMGQRScannerContract.View, payload: OMGResponse<TransactionRequest>) {

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
