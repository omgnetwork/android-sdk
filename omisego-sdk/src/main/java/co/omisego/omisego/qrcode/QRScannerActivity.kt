package co.omisego.omisego.qrcode

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.omisego.omisego.R
import com.google.zxing.integration.android.IntentIntegrator

class QRScannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)

        val intentIntegrator = IntentIntegrator(this)
        intentIntegrator.initiateScan()
    }
}
