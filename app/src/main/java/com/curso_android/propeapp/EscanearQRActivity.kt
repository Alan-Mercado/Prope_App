package com.curso_android.propeapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityEscanearQractivityBinding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class EscanearQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEscanearQractivityBinding
    private lateinit var cameraProvider: ProcessCameraProvider
    private var processingBarcode = false

    private lateinit var destino: String
    private lateinit var nivel_acceso: String

    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEscanearQractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        destino = intent.getStringExtra(AppUtils.StringKeys.BOTON_CONST) ?: AppUtils.StringKeys.ERROR_CONST
        nivel_acceso = intent.getStringExtra(AppUtils.StringKeys.NIVEL_ACCESO_CONST) ?: AppUtils.StringKeys.ERROR_CONST

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
        }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            val analysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                processImageProxy(imageProxy)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysis)

        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        if (processingBarcode) {
            imageProxy.close()
            return
        }

        processingBarcode = true

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()

            val scanner = BarcodeScanning.getClient(options)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        if (!rawValue.isNullOrEmpty()) {
                            imageProxy.close()
                            scanner.close()
                            cameraProvider.unbindAll()
                            onQRCodeScanned(rawValue, destino)
                            return@addOnSuccessListener
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error escaneando QR", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener {
                    processingBarcode = false
                    imageProxy.close()
                }
        } else {
            processingBarcode = false
            imageProxy.close()
        }
    }

    /*private fun onQRCodeScanned(valorQR: String) {
        // Aquí asumimos que el QR contiene el número de registro directamente
        val intent = Intent(this, BuscarAlumActivity::class.java).apply {
            putExtra(AppUtils.StringKeys.ESTUDIANTE_CONST, valorQR)
            putExtra(AppUtils.StringKeys.BOTON_CONST, AppUtils.StringKeys.BUSCAR_CONST)
        }
        startActivity(intent)
        finish()
    }*/

    private fun onQRCodeScanned(valor_QR:String, donde_ir:String) {
        //navegar a Mostrar Info Estudiante
        if (donde_ir == AppUtils.StringKeys.BUSCAR_CONST) {
            val intent = Intent(this, InfoAlumActivity::class.java)
            intent.putExtra(AppUtils.StringKeys.ESTUDIANTE_CONST, valor_QR)
            startActivity(intent)

            //navegar a Editar Estudiante
        } else if (donde_ir == AppUtils.StringKeys.EDITAR_ESTUDIANTE_CONST){
            val intent = Intent(this, EditarAlumActivity::class.java)
            intent.putExtra(AppUtils.StringKeys.ESTUDIANTE_CONST, valor_QR)
            intent.putExtra(AppUtils.StringKeys.NIVEL_ACCESO_CONST, nivel_acceso)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun regresar() {
        finish()
    }
}
