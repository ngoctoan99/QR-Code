package com.example.qrcode

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import java.util.jar.Manifest
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private lateinit var ScannerQr : CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val actionbar = supportActionBar
        actionbar!!.hide()
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),123)
        }else {
            Scanning()
        }
    }

    private fun Scanning() {
        val scannerView : CodeScannerView = findViewById(R.id.scannerqr)
        ScannerQr = CodeScanner(this,scannerView)
        ScannerQr.camera = CodeScanner.CAMERA_BACK
        ScannerQr.formats = CodeScanner.ALL_FORMATS
        ScannerQr.autoFocusMode = AutoFocusMode.SAFE
        ScannerQr.scanMode = ScanMode.SINGLE
        ScannerQr.isAutoFocusEnabled = true
        ScannerQr.isFlashEnabled = false
        ScannerQr.decodeCallback = DecodeCallback {
            runOnUiThread {
                if(validationURL(it.text)){
                    startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(it.text)))
                }else {
                    Toast.makeText(this,"Scan Result : ${it.text}", Toast.LENGTH_LONG).show()
                    Log.d("toan",it.text.toString())
                }
            }
        }
        ScannerQr.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error : ${it.message}",Toast.LENGTH_LONG).show()
            }
        }
        scannerView.setOnClickListener {
            ScannerQr.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 123){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Camera permission granted ",Toast.LENGTH_SHORT).show()
                Scanning()
            }else {
                Toast.makeText(this,"Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(::ScannerQr.isInitialized){
            ScannerQr?.startPreview()
        }
    }

    override fun onPause() {
        if(::ScannerQr.isInitialized){
            ScannerQr?.releaseResources()
        }
        super.onPause()
    }
    fun validationURL(url: String?): Boolean {
        val regex = "[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)"
        val p = Pattern.compile(regex)
        val m = p.matcher(url)
        return m.find()
    }
}