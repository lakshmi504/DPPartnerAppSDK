package com.dpdelivery.android.technicianui.scanner

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.dpdelivery.android.R
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import dagger.android.support.DaggerAppCompatActivity

class ScannerActivity : DaggerAppCompatActivity(), DecoratedBarcodeView.TorchListener {
    private var capture: CaptureManager? = null
    private var barcodeScannerView: DecoratedBarcodeView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        //Initialize barcode scanner view
        barcodeScannerView = findViewById<View>(R.id.zxing_barcode_scanner) as DecoratedBarcodeView?
        barcodeScannerView!!.setStatusText("Place a Qr code inside the viewfinder rectangle to scan it.")
        //start capture
        capture = CaptureManager(this, barcodeScannerView!!)
        capture!!.initializeFromIntent(intent, savedInstanceState)
        capture!!.decode()
    }

    override fun onResume() {
        super.onResume()
        capture!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture!!.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return barcodeScannerView!!.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    override fun onTorchOn() {}

    override fun onTorchOff() {}
}
