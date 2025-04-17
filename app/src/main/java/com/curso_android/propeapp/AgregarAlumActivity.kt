package com.curso_android.propeapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityAgregarAlumBinding
import com.google.firebase.database.*

class AgregarAlumActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference//Referencia a Firebase

    private lateinit var binding: ActivityAgregarAlumBinding

    private lateinit var opcionesEstatus: List<String>
    private lateinit var opcionesCredencial: List<String>
    private lateinit var opcionesGrupo: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAgregarAlumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        //database = FirebaseDatabase.getInstance().reference//Inicializar referencia a BD
        database = AppUtils.database

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        opcionesEstatus = listOf("Pagado", "No pagado", "Prorroga")//poner en apputils.stringkeys??????
        opcionesCredencial = listOf("Pendiente", "Entregada")//poner en apputils.stringkeys??????
        opcionesGrupo = listOf("G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8", "G9", "G10", "G11", "G12")//poner en apputils.stringkeys??????

        //Creo que esto se puede optimizar
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

    private fun initUI() {
        //agregar alum
        binding.btnAgregarAlum.setOnClickListener { agregarAlum() }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun agregarAlum() {
        val nombre = binding.etNombre.text.toString().trim()
        val registro = binding.etRegistro.text.toString().trim()
        //val estatus = binding.etEstatus.text.toString().trim()
        val estatus = binding.spEstatus.selectedItem.toString()
        //val grupo = binding.etGrupo.text.toString().trim()
        val grupo = binding.spGrupo.selectedItem.toString()
        val contacto = binding.etContacto.text.toString().trim()
        val tel_1 = binding.etTelefono1.text.toString().trim()
        //val tutor_2 = binding.etTutor2.text.toString().trim().ifEmpty { "No disponible" }
        //val tel_2 = binding.etTelefono2.text.toString().trim().ifEmpty { "No disponible" }
        //val cred_entr = binding.etCredencial.text.toString().trim().ifEmpty { "Pendiente" }
        val cred_entr = binding.spCredencial.selectedItem.toString()

        if (nombre.isEmpty() || registro.isEmpty() || estatus.isEmpty() || grupo.isEmpty()
            || contacto.isEmpty() || tel_1.isEmpty() || cred_entr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val alumno = Alumno(registro, acceso = AppUtils.StringKeys.ESTUDIANTE_CONST, nombre, password = AppUtils.hashSHA256(AppUtils.StringKeys.PASS_PREDETERMINADA_CONST), estatus, grupo, contacto, tel_1, /*tutor_2, tel_2,*/ cred_entr)
        //val alumno = Alumno(admin = false, nombre, password = AppUtils.hashSHA256(AppUtils.StringKeys.PASS_PREDETERMINADA_CONST), estatus, grupo, tutor_1, tel_1, tutor_2, tel_2)

        /*if (registro != null) {
            database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro).setValue(alumno).addOnSuccessListener {
                Toast.makeText(this, "Alumno agregado correctamente", Toast.LENGTH_SHORT).show()
                limpiarCampos()
            }.addOnFailureListener {
                Toast.makeText(this, "Error al agregar el alumno", Toast.LENGTH_SHORT).show()
            }
        }*/

        // Verificar si ya existe un registro con ese ID en la base de datos
        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Si el registro ya existe
                    Toast.makeText(this@AgregarAlumActivity, "Ya existe este registro", Toast.LENGTH_SHORT).show()
                } else {
                    // Si el registro no existe agregar el nuevo alumno
                    database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro).setValue(alumno)
                        .addOnSuccessListener {
                            Toast.makeText(this@AgregarAlumActivity, "Alumno agregado correctamente", Toast.LENGTH_SHORT).show()
                            limpiarCampos()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@AgregarAlumActivity, "Error al agregar el alumno", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AgregarAlumActivity, "Error al verificar el registro", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun limpiarCampos() {
        binding.etNombre.text.clear()
        binding.etRegistro.text.clear()
        //binding.etEstatus.text.clear()
        binding.spEstatus.setSelection(0)
        //binding.etGrupo.text.clear()
        binding.spGrupo.setSelection(0)
        binding.etContacto.text.clear()
        binding.etTelefono1.text.clear()
        //binding.etTutor2.text.clear()
        //binding.etTelefono2.text.clear()
        //binding.etCredencial.text.clear()
        binding.spCredencial.setSelection(0)
    }

    private fun regresar(){
        finish()
    }
}
