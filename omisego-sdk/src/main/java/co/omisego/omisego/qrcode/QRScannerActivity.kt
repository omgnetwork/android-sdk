package co.omisego.omisego.qrcode

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.omisego.omisego.R
import co.omisego.omisego.custom.camera.ui.CameraPreview
import kotlinx.android.synthetic.main.activity_qrscanner.*

class QRScannerActivity : AppCompatActivity() {
    private lateinit var mPreview: CameraPreview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)
        scannerView.startCamera()
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
