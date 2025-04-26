package com.curso_android.propeapp

import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityAgregarEstudBinding
import com.google.firebase.database.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AgregarEstudActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference//Referencia a Firebase

    private lateinit var binding: ActivityAgregarEstudBinding

    private lateinit var opcionesEstatus: List<String>
    private lateinit var opcionesCredencial: List<String>
    private lateinit var opcionesGrupo: List<String>

    @RequiresApi(Build.VERSION_CODES.O)//esta linea es una advertencia-comprobacion para versiones anteriores de Android
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAgregarEstudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        database = AppUtils.database

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        opcionesEstatus = listOf("Pagado", "No pagado", "Prorroga")
        opcionesCredencial = listOf("Pendiente", "Entregada")
        opcionesGrupo = listOf("G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8", "G9", "G10", "G11", "G12")

        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesEstatus)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spEstatus.adapter = adapter1
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesCredencial)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCredencial.adapter = adapter2
        val adapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesGrupo)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spGrupo.adapter = adapter3

        initUI()
    }

    @RequiresApi(Build.VERSION_CODES.O)//esta linea es una advertencia-comprobacion para versiones anteriores de Android
    private fun initUI() {
        //agregar alum
        binding.btnAgregarAlum.setOnClickListener { agregarAlum() }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    @RequiresApi(Build.VERSION_CODES.O)//esta linea es una advertencia-comprobacion para versiones anteriores de Android
    private fun agregarAlum() {
        val nombre = binding.etNombre.text.toString().trim()
        val registro = binding.etRegistro.text.toString().trim()
        val estatus = binding.spEstatus.selectedItem.toString()
        val grupo = binding.spGrupo.selectedItem.toString()
        val contacto = binding.etContacto.text.toString().trim()
        val tel_1 = binding.etTelefono1.text.toString().trim()
        //val tutor_2 = binding.etTutor2.text.toString().trim().ifEmpty { "No disponible" }
        //val tel_2 = binding.etTelefono2.text.toString().trim().ifEmpty { "No disponible" }
        val cred_entr = binding.spCredencial.selectedItem.toString()

        if (nombre.isEmpty() || registro.isEmpty() || estatus.isEmpty() || grupo.isEmpty()
            || contacto.isEmpty() || tel_1.isEmpty() || cred_entr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val asistenciasIniciales = obtenerFechaCreacion()//obtenemos la fecha de creacion

        val estudiante = Estudiante(registro, acceso = AppUtils.StringKeys.ESTUDIANTE_CONST, nombre, password = AppUtils.hashSHA256(AppUtils.StringKeys.PASS_PREDETERMINADA_CONST), estatus, grupo, contacto, tel_1, /*tutor_2, tel_2,*/ cred_entr, asistenciasIniciales)

        //Verificar si ya existe un registro
        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //Si el registro ya existe
                    Toast.makeText(this@AgregarEstudActivity, "Ya existe este registro", Toast.LENGTH_SHORT).show()
                } else {
                    //Si el registro no existe, agregar el nuevo alumno
                    database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro).setValue(estudiante)
                        .addOnSuccessListener {
                            Toast.makeText(this@AgregarEstudActivity, "Estudiante agregado correctamente", Toast.LENGTH_SHORT).show()
                            limpiarCampos()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@AgregarEstudActivity, "Error al agregar al estudiante", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AgregarEstudActivity, "Error al verificar el registro", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun obtenerFechaCreacion(): MutableMap<String, String> {
        //obtener fecha y hora actuales y formatearlas
        val currentDateTime = LocalDateTime.now()
        val formatterKey = DateTimeFormatter.ofPattern("dd-MM-yy_HH-mm-ss")
        val formatterValue = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss")

        val clave = formatterKey.format(currentDateTime)
        val valor = formatterValue.format(currentDateTime)

        val asistenciasIniciales = mutableMapOf<String, String>(
            clave to "$valor CREACION"
        )

        return asistenciasIniciales
    }

    private fun limpiarCampos() {
        binding.etNombre.text.clear()
        binding.etRegistro.text.clear()
        binding.spEstatus.setSelection(0)
        binding.spGrupo.setSelection(0)
        binding.etContacto.text.clear()
        binding.etTelefono1.text.clear()
        //binding.etTutor2.text.clear()
        //binding.etTelefono2.text.clear()
        binding.spCredencial.setSelection(0)
    }

    private fun regresar(){
        finish()
    }
}
