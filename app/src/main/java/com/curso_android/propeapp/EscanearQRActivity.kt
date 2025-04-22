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

    private lateinit var proveedor_camara: ProcessCameraProvider
    private var procesar_QR = false//variable que se inicializa como false desde el inicio

    private lateinit var destino: String
    private lateinit var nivel_acceso: String

    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 1001//constante para buscar el codigo de permiso en el dispositivo
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

        initUI()
    }

    private fun initUI(){
        //Revisamos si ya se concedio el permiso para utilizar la camara con anterioridad
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            iniciarCamara()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
        }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    //Funcion para pedir permiso de utilizar la camara
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            iniciarCamara()
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun iniciarCamara() {
        val camara_provider = ProcessCameraProvider.getInstance(this)
        camara_provider.addListener({
            proveedor_camara = camara_provider.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            val analisis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analisis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                procesarImagen(imageProxy)
            }

            val selector_camara = CameraSelector.DEFAULT_BACK_CAMERA//inicializar camara trasera

            proveedor_camara.unbindAll()
            proveedor_camara.bindToLifecycle(this, selector_camara, preview, analisis)

        }, ContextCompat.getMainExecutor(this))
    }

    //Funcion que recupera el QR en la imagen
    @OptIn(ExperimentalGetImage::class)
    private fun procesarImagen(imagen_proxy: ImageProxy) {
        if (procesar_QR) {
            imagen_proxy.close()
            return
        }

        procesar_QR = true

        val media_imagen = imagen_proxy.image
        if (media_imagen != null) {
            val imagen = InputImage.fromMediaImage(media_imagen, imagen_proxy.imageInfo.rotationDegrees)
            val opciones = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()

            val scanner = BarcodeScanning.getClient(opciones)
            scanner.process(imagen)
                .addOnSuccessListener { qrs ->
                    for (qr in qrs) {
                        val qr_recibido = qr.rawValue
                        if (!qr_recibido.isNullOrEmpty()) {
                            imagen_proxy.close()
                            scanner.close()
                            proveedor_camara.unbindAll()
                            alEscanearQR(qr_recibido, destino)
                            return@addOnSuccessListener
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error escaneando QR", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener {
                    procesar_QR = false
                    imagen_proxy.close()
                }
        } else {
            procesar_QR = false
            imagen_proxy.close()
        }
    }

    //Funcion que envia a la info del estudiante al recibir el QR
    private fun alEscanearQR(valor_QR:String, donde_ir:String) {
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

    private fun regresar() {
        finish()
    }
}
