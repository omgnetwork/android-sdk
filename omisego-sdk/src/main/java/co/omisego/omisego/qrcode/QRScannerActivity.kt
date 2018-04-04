package co.omisego.omisego.qrcode

import android.hardware.Camera
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.omisego.omisego.R
import co.omisego.omisego.custom.camera.ui.CameraPreview
import co.omisego.omisego.custom.zxing.ui.OMGQRScannerView
import kotlinx.android.synthetic.main.activity_qrscanner.*

class QRScannerActivity : AppCompatActivity(), Camera.PreviewCallback {
    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {

//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mPreview: CameraPreview

    private lateinit var scannerView: OMGQRScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)
        scannerView = OMGQRScannerView(this)
        cameraPreview.addView(scannerView)
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
