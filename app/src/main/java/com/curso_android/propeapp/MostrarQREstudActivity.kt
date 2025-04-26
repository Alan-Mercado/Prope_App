package com.curso_android.propeapp

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityMostrarQrestudBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class MostrarQREstudActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMostrarQrestudBinding

    private lateinit var registro: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMostrarQrestudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        registro = intent.getStringExtra(AppUtils.StringKeys.ESTUDIANTE_CONST) ?: AppUtils.StringKeys.ERROR_CONST

        initUI()
    }

    private fun initUI() {
        //generamos codigo QR
        generarQR()

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun generarQR() {
        if (registro == null) {
            Toast.makeText(this, "Registro no recibido", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.encodeBitmap(
                registro,
                BarcodeFormat.QR_CODE,
                500,
                500
            )
            binding.ivQR.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Toast.makeText(this, "Error al generar QR", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun regresar() {
        finish()
    }
}