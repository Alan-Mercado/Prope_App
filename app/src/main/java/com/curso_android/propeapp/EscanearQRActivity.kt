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
import com.google.firebase.database.DatabaseReference
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EscanearQRActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

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

        database = AppUtils.database

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
        val camaraProvider = ProcessCameraProvider.getInstance(this)
        camaraProvider.addListener({
            proveedor_camara = camaraProvider.get()

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

            val selectorCamara = CameraSelector.DEFAULT_BACK_CAMERA//inicializar camara trasera

            proveedor_camara.unbindAll()
            proveedor_camara.bindToLifecycle(this, selectorCamara, preview, analisis)

        }, ContextCompat.getMainExecutor(this))
    }

    //Funcion que recupera el QR en la imagen
    @OptIn(ExperimentalGetImage::class)
    private fun procesarImagen(imagenProxy: ImageProxy) {
        if (procesar_QR) {
            imagenProxy.close()
            return
        }

        procesar_QR = true

        val mediaImagen = imagenProxy.image
        if (mediaImagen != null) {
            val imagen = InputImage.fromMediaImage(mediaImagen, imagenProxy.imageInfo.rotationDegrees)
            val opciones = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()

            val scanner = BarcodeScanning.getClient(opciones)
            scanner.process(imagen)
                .addOnSuccessListener { qrs ->
                    for (qr in qrs) {
                        val qrRecibido = qr.rawValue
                        if (!qrRecibido.isNullOrEmpty()) {
                            imagenProxy.close()
                            scanner.close()
                            proveedor_camara.unbindAll()
                            alEscanearQR(qrRecibido, destino)
                            return@addOnSuccessListener
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error escaneando QR", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener {
                    procesar_QR = false
                    imagenProxy.close()
                }
        } else {
            procesar_QR = false
            imagenProxy.close()
        }
    }

    //Funcion que envia a la info del estudiante al recibir el QR
    private fun alEscanearQR(valorQR:String, donde_ir:String) {
        registrarEscaneo(valorQR)//registramos la fecha y hora del escaneo

        //navegar a Mostrar Info Estudiante
        if (donde_ir == AppUtils.StringKeys.BUSCAR_CONST) {
            val intent = Intent(this, InfoEstudActivity::class.java)
            intent.putExtra(AppUtils.StringKeys.ESTUDIANTE_CONST, valorQR)
            startActivity(intent)

            //navegar a Editar Estudiante
        } else if (donde_ir == AppUtils.StringKeys.EDITAR_ESTUDIANTE_CONST){
            val intent = Intent(this, EditarEstudActivity::class.java)
            intent.putExtra(AppUtils.StringKeys.ESTUDIANTE_CONST, valorQR)
            intent.putExtra(AppUtils.StringKeys.NIVEL_ACCESO_CONST, nivel_acceso)
            startActivity(intent)
        }
    }

    fun registrarEscaneo(registro: String) {
        val usuario = database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro)

        //Obtener la fecha y hora actual
        val now = Date()
        val formatoClave = SimpleDateFormat("dd-MM-yy_HH-mm-ss", Locale.getDefault())
        val formatoValor = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.getDefault())

        val clave = formatoClave.format(now)
        val valor = formatoValor.format(now) + " ESCANEO"

        val asistenciaMap = mapOf<String, Any>(
            AppUtils.DatabaseKeys.ASISTENCIAS_DB_CONST + "/$clave" to valor
        )

        usuario.updateChildren(asistenciaMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this,"Escaneo registrado correctamente",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"Error al registrar la asistencia",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun regresar() {
        finish()
    }
}
