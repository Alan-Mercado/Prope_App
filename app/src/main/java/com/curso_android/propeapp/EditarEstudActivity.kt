package com.curso_android.propeapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.curso_android.propeapp.databinding.ActivityEditarEstudBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditarEstudActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityEditarEstudBinding

    private lateinit var user: String
    private lateinit var nivel_acceso: String

    private lateinit var opcionesEstatus: List<String>
    private lateinit var opcionesCredencial: List<String>
    private lateinit var opcionesGrupo: List<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditarEstudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppUtils.database

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = intent.getStringExtra(AppUtils.StringKeys.ESTUDIANTE_CONST) ?: AppUtils.StringKeys.ERROR_CONST
        nivel_acceso = intent.getStringExtra(AppUtils.StringKeys.NIVEL_ACCESO_CONST) ?: AppUtils.StringKeys.ERROR_CONST

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

    private fun initUI() {
        mostrarDatos()

        binding.btnActualizarAlum.setOnClickListener{ actualizarEstud() }

        binding.btnAsistencias.setOnClickListener { navegarAsistencias(user) }

        binding.btnRestablecerContrasenia.setOnClickListener {
            AlertDialog.Builder(it.context)
                .setTitle("¿Estás seguro?")
                .setMessage("¿Deseas RESTABLECER la contraseña de este usuario?")
                .setPositiveButton("Continuar") { dialog, _ ->
                    AppUtils.restablecerPassword(user) { exito ->
                        if (exito){
                            Toast.makeText(this, "Contraseña reestablecida correctamente a '" + AppUtils.StringKeys.PASS_PREDETERMINADA_CONST + "'", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error al intentar restablecer contraseña", Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                    }
                }
                .setNegativeButton("Regresar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

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
                val contacto = snapshot.child(AppUtils.DatabaseKeys.CONTACTO_DB_CONST).getValue(String::class.java) ?: "No disponible"
                val tel_1 = snapshot.child(AppUtils.DatabaseKeys.TEL_1_DB_CONST).getValue(String::class.java) ?: "No disponible"
                //val tutor_2 = snapshot.child(AppUtils.DatabaseKeys.TUTOR_2_DB_CONST).getValue(String::class.java) ?: "No disponible"
                //val tel_2 = snapshot.child(AppUtils.DatabaseKeys.TEL_2_DB_CONST).getValue(String::class.java) ?: "No disponible"
                val cred_entr = snapshot.child(AppUtils.DatabaseKeys.CREDENCIAL_ENTREGADA_DB_CONST).getValue(String::class.java) ?: "Pendiente"

                //asignamos valores recuparados a los textviews
                binding.etNombre.setText(nombre)
                binding.etRegistro.setText(user)
                val pos1 = opcionesEstatus.indexOf(estatus)
                if (pos1 >= 0) {
                    binding.spEstatus.setSelection(pos1)
                }
                val pos2 = opcionesGrupo.indexOf(grupo)
                if (pos2 >= 0) {
                    binding.spGrupo.setSelection(pos2)
                }
                binding.etContacto.setText(contacto)
                binding.etTelefono1.setText(tel_1)
                //binding.etTutor2.setText(tutor_2)
                //binding.etTelefono2.setText(tel_2)
                val pos3 = opcionesCredencial.indexOf(cred_entr)
                if (pos3 >= 0) {
                    binding.spCredencial.setSelection(pos3)
                }

                //BOTON PARA ELIMINAR EL REGISTRO ACTUAL (solo admin)
                if (nivel_acceso == AppUtils.StringKeys.ADMIN_CONST){
                    binding.btnEliminarAlum.setOnClickListener {
                        AlertDialog.Builder(it.context)
                            .setTitle("¿Estás seguro?")
                            .setMessage("¿Deseas eliminar este estudiante PERMANENTEMENTE?")
                            .setPositiveButton("Continuar") { dialog, _ ->
                                eliminarEstudiante(user)
                                dialog.dismiss()
                            }
                            .setNegativeButton("Regresar") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                } else {
                    binding.btnEliminarAlum.isVisible = false
                }
            } else {
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarEstud() {
        val nombre = binding.etNombre.text.toString().trim()
        val registro = binding.etRegistro.text.toString().trim()
        val estatus = binding.spEstatus.selectedItem.toString()
        val grupo = binding.spGrupo.selectedItem.toString()
        val tutor_1 = binding.etContacto.text.toString().trim()
        val tel_1 = binding.etTelefono1.text.toString().trim()
        //val tutor_2 = binding.etTutor2.text.toString().trim().ifEmpty { "No disponible" }
        //val tel_2 = binding.etTelefono2.text.toString().trim().ifEmpty { "No disponible" }
        val cred_entr = binding.spCredencial.selectedItem.toString()

        if (nombre.isEmpty() || registro.isEmpty() || estatus.isEmpty() || grupo.isEmpty()
            || tutor_1.isEmpty() || tel_1.isEmpty() || cred_entr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(user).addListenerForSingleValueEvent(object :
            ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                //si se cambio el campo "registro"
                if (registro != user) {
                    database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro)
                        .get().addOnSuccessListener { newSnapshot ->
                            //si el registro ya esta siendo utilizado
                            if (newSnapshot.exists()) {
                                Toast.makeText(this@EditarEstudActivity, "Ya existe un estudiante con ese registro. Elimínalo si deseas continuar", Toast.LENGTH_SHORT).show()
                            //si el registro esta libre
                            } else {
                                if (snapshot.exists()) {
                                    val asistenciasModificadas = obtenerFechaActualizacion(snapshot)

                                    val estudiante = Estudiante(registro, acceso = snapshot.child(AppUtils.DatabaseKeys.ACCESO_DB_CONST).value.toString(), nombre, password = snapshot.child(AppUtils.DatabaseKeys.PASSWORD_DB_CONST).value.toString(), estatus, grupo, tutor_1, tel_1, /*tutor_2, tel_2,*/ cred_entr, asistenciasModificadas)
                                    database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro).setValue(estudiante)
                                        .addOnSuccessListener {
                                            val registro_viejo = snapshot.child(AppUtils.DatabaseKeys.REGISTRO_DB_CONST).getValue(String::class.java) ?: AppUtils.StringKeys.ERROR_CONST
                                            eliminarEstudiante(registro_viejo)//se elimina al viejo usuario (con el registro viejo) de la base de datos

                                            Toast.makeText(this@EditarEstudActivity, "Estudiante actualizado correctamente", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this@EditarEstudActivity, "Error al actualizar al estudiante", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(this@EditarEstudActivity, "No existe este registro", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                } else {
                    if (snapshot.exists()) {
                        //si el registro ya existe
                        val asistenciasModificadas = obtenerFechaActualizacion(snapshot)

                        val estudiante = Estudiante(registro, acceso = snapshot.child(AppUtils.DatabaseKeys.ACCESO_DB_CONST).value.toString(), nombre, password = snapshot.child(AppUtils.DatabaseKeys.PASSWORD_DB_CONST).value.toString(), estatus, grupo, tutor_1, tel_1, /*tutor_2, tel_2,*/ cred_entr, asistenciasModificadas)
                        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro).setValue(estudiante)
                            .addOnSuccessListener {
                                Toast.makeText(this@EditarEstudActivity, "Estudiante actualizado correctamente", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@EditarEstudActivity, "Error al actualizar al estudiante", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        //si el registro no existe
                        Toast.makeText(this@EditarEstudActivity, "No existe este registro", Toast.LENGTH_SHORT).show()
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditarEstudActivity, "Error al verificar el registro", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun eliminarEstudiante(registro: String) {
        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Estudiante eliminado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Error al eliminar: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenerFechaActualizacion(snapshot: DataSnapshot): MutableMap<String, String> {
        val asistenciasExistentes = snapshot.child(AppUtils.DatabaseKeys.ASISTENCIAS_DB_CONST).value as? Map<String, String> ?: emptyMap()
        val asistenciasActualizadas = asistenciasExistentes.toMutableMap()

        val timestamp = SimpleDateFormat("dd-MM-yy_HH:mm:ss", Locale.getDefault()).format(Date())
        val readable = timestamp.replace('_', ' ') + " ACTUALIZACION"

        asistenciasActualizadas[timestamp] = readable

        return asistenciasActualizadas
    }

    private fun navegarAsistencias(registro:String) {
        val intent = Intent(this, MostrarAsistenciasActivity::class.java)
        intent.putExtra(AppUtils.DatabaseKeys.REGISTRO_DB_CONST, registro)
        startActivity(intent)
    }

    private fun regresar() {
        finish()
    }
}