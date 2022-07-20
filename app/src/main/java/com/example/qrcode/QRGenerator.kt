package com.example.qrcode

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class QRGenerator : AppCompatActivity() {
    private lateinit var btnGenerator : Button
    private lateinit var btnBack : Button
    private lateinit var editGenerator : EditText
    private lateinit var imageQrCode : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrgenerator)
        val actionBar  = supportActionBar
        actionBar!!.hide()
        btnGenerator = findViewById(R.id.btngenerator)
        editGenerator = findViewById(R.id.editQrcode)
        imageQrCode = findViewById(R.id.imageQrcode)
        btnBack = findViewById(R.id.btnback)
        imageQrCode.setOnClickListener {
            if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.M){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),100)
                }
                else {
                    saveImage()
                }
            }
            else {
                saveImage()
            }
        }
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        btnGenerator.setOnClickListener {
            val data = editGenerator.text.toString().trim()
            if(data.isEmpty()){
                Toast.makeText(this, "Please do not leave data blank", Toast.LENGTH_SHORT).show()
            }else {
                val write = QRCodeWriter()
                try {
                    val bitMatrix = write.encode(data, BarcodeFormat.QR_CODE, 512,512)
                    val width = bitMatrix.width
                    val height = bitMatrix.height
                    val bmp = Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565)
                    for(x in 0 until width){
                        for (y in 0 until height){
                            bmp.setPixel(x,y, if(bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                        }
                    }
                    imageQrCode.setImageBitmap(bmp)
                    editGenerator.setText("")
                }catch (e: WriterException){
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 100){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                saveImage()
            }else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    fun saveImage(){
        val externalStorageState = Environment.getExternalStorageState()
        if(externalStorageState.equals(Environment.MEDIA_MOUNTED)){
            val storageDirectory = Environment.getExternalStorageDirectory().toString()
            val file = File(storageDirectory,"qr_code.jpg")
            try {
                val stream : OutputStream = FileOutputStream(file)
                var bitmap : Bitmap = (imageQrCode.drawable as BitmapDrawable).bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
                stream.flush()
                stream.close()
                MediaStore.Images.Media.insertImage(contentResolver, file.absolutePath,file.name,file.name)
                Toast.makeText(this, "Save image successful ${Uri.parse(file.absolutePath)}", Toast.LENGTH_SHORT).show()
            }catch (e : Exception){
                e.printStackTrace()
            }
        }else {
            Toast.makeText(this, "Fail to save image", Toast.LENGTH_SHORT).show()
        }
    }
}