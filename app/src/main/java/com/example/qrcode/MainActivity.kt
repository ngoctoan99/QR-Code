package com.example.qrcode

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private lateinit var  btnScanner : Button
    private lateinit var  btnGenerator : Button
    private lateinit var tvclick : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnScanner = findViewById(R.id.btnToScanner)
        btnGenerator = findViewById(R.id.btnToGenerator)
        tvclick = findViewById(R.id.clickhere)
        val actionBar = supportActionBar
        actionBar!!.hide()
        btnScanner.setOnClickListener {
            val intent = Intent(this, QRScannerActivity::class.java)
            startActivity(intent)
        }
        btnGenerator.setOnClickListener {
            val intent = Intent(this, QRGenerator::class.java)
            startActivity(intent)
        }
        tvclick.setOnClickListener {
            val bottomSheet : BottomSheetDialog = BottomSheetDialog(this,R.style.BottomSheetStyle)
            bottomSheet.setContentView(R.layout.dialog)
            val btnface = bottomSheet.findViewById<TextView>(R.id.btnface)
            val btnCancle = bottomSheet.findViewById<TextView>(R.id.btnCancle)
            btnface?.setOnClickListener {
                val uri :String = "https://www.facebook.com/toan21101999/"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(uri))
                startActivity(intent)
            }
            btnCancle?.setOnClickListener {
                bottomSheet.dismiss()
            }
            bottomSheet.show()
        }
    }
}