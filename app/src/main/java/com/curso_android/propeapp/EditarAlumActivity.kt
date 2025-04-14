package com.curso_android.propeapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityEditarAlumBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class EditarAlumActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityEditarAlumBinding

    private lateinit var user: String

    private lateinit var opcionesEstatus: List<String>
    private lateinit var opcionesCredencial: List<String>
    private lateinit var opcionesGrupo: List<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditarAlumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppUtils.database//Inicializar referencia a BD

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = intent.getStringExtra(AppUtils.StringKeys.USUARIO_CONST) ?: AppUtils.StringKeys.ERROR_CONST

        opcionesEstatus = listOf("Pagado", "No pagado", "Prorroga")
        opcionesCredencial = listOf("Pendiente", "Entregada")
        opcionesGrupo = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")//poner en apputils.stringkeys??????

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
        mostrarDatos()

        binding.btnActualizarAlum.setOnClickListener{ actualizarAlum() }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun mostrarDatos() {
        if (user == AppUtils.StringKeys.ERROR_CONST) {
            Toast.makeText(this, "Error al recuperar usuario", Toast.LENGTH_SHORT).show()
            return
        }

        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(user).get().addOnSuccessListener {
                snapshot ->
            if (snapshot.exists()) {
                //recuperamos datos de la BD y asignamos valores por defecto
                val nombre = snapshot.child(AppUtils.DatabaseKeys.NOMBRE_DB_CONST).getValue(String::class.java) ?: "Desconocido"
                //val registro = database.child("Usuarios").child(user).get().toString()
                //val registro = user
                val estatus = snapshot.child(AppUtils.DatabaseKeys.ESTATUS_DB_CONST).getValue(String::class.java) ?: "No registrado"
                val grupo = snapshot.child(AppUtils.DatabaseKeys.GRUPO_DB_CONST).getValue(String::class.java) ?: "N/A"
                val tutor_1 = snapshot.child(AppUtils.DatabaseKeys.TUTOR_1_DB_CONST).getValue(String::class.java) ?: "No disponible"
                val tel_1 = snapshot.child(AppUtils.DatabaseKeys.TEL_1_DB_CONST).getValue(String::class.java) ?: "No disponible"
                val tutor_2 = snapshot.child(AppUtils.DatabaseKeys.TUTOR_2_DB_CONST).getValue(String::class.java) ?: "No disponible"
                val tel_2 = snapshot.child(AppUtils.DatabaseKeys.TEL_2_DB_CONST).getValue(String::class.java) ?: "No disponible"
                val cred_entr = snapshot.child(AppUtils.DatabaseKeys.CREDENCIAL_ENTREGADA).getValue(String::class.java) ?: "Pendiente"

                //Asignamos valores recuparados a los textviews
                binding.etNombre.setText(nombre)
                binding.etRegistro.setText(user)//cambiar a campo de la base de datos???????????????????
                //binding.etEstatus.setText(estatus)
                val pos1 = opcionesEstatus.indexOf(estatus)
                if (pos1 >= 0) {
                    binding.spEstatus.setSelection(pos1)
                }
                //binding.etGrupo.setText(grupo)
                val pos2 = opcionesGrupo.indexOf(grupo)
                if (pos2 >= 0) {
                    binding.spGrupo.setSelection(pos2)
                }
                binding.etTutor1.setText(tutor_1)
                binding.etTelefono1.setText(tel_1)
                binding.etTutor2.setText(tutor_2)
                binding.etTelefono2.setText(tel_2)
                //binding.etCredencial.setText(cred_entr)
                val pos3 = opcionesCredencial.indexOf(cred_entr)
                if (pos3 >= 0) {
                    binding.spCredencial.setSelection(pos3)
                }
            } else {
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarAlum() {
        val nombre = binding.etNombre.text.toString().trim()
        val registro = binding.etRegistro.text.toString().trim()
        //val estatus = binding.etEstatus.text.toString().trim()
        val estatus = binding.spEstatus.selectedItem.toString()
        //val grupo = binding.etGrupo.text.toString().trim()
        val grupo = binding.spGrupo.selectedItem.toString()
        val tutor_1 = binding.etTutor1.text.toString().trim()
        val tel_1 = binding.etTelefono1.text.toString().trim()
        val tutor_2 = binding.etTutor2.text.toString().trim().ifEmpty { "No disponible" }
        val tel_2 = binding.etTelefono2.text.toString().trim().ifEmpty { "No disponible" }
        //val cred_entr = binding.etCredencial.text.toString().trim().ifEmpty { "Pendiente" }
        val cred_entr = binding.spCredencial.selectedItem.toString()

        if (nombre.isEmpty() || registro.isEmpty() || estatus.isEmpty() || grupo.isEmpty()
            || tutor_1.isEmpty() || tel_1.isEmpty() || cred_entr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si ya existe un registro con ese ID en la base de datos
        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(user).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Si el registro ya existe
                    val alumno = Alumno(registro, admin = false, nombre, password = snapshot.child(AppUtils.DatabaseKeys.PASSWORD_DB_CONST).value.toString(), estatus, grupo, tutor_1, tel_1, tutor_2, tel_2, cred_entr)
                    database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro).setValue(alumno)
                        .addOnSuccessListener {
                            Toast.makeText(this@EditarAlumActivity, "Alumno actualizado correctamente", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@EditarAlumActivity, "Error al actualizar el alumno", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Si el registro no existe
                    Toast.makeText(this@EditarAlumActivity, "No existe este registro", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditarAlumActivity, "Error al verificar el registro", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun regresar() {
        finish()
    }
}