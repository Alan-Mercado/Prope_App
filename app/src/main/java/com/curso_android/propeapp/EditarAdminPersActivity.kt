package com.curso_android.propeapp

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityEditarAdminPersBinding
import com.google.firebase.database.*

class EditarAdminPersActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityEditarAdminPersBinding

    private lateinit var user: String

    private lateinit var opcionesAcceso: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditarAdminPersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppUtils.database

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = intent.getStringExtra(AppUtils.StringKeys.SERVICIO_SOCIAL_CONST) ?: AppUtils.StringKeys.ERROR_CONST

        opcionesAcceso = listOf(AppUtils.StringKeys.ADMIN_CONST, AppUtils.StringKeys.SERVICIO_SOCIAL_CONST)

        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesAcceso)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spAcceso.adapter = adapter1

        initUI()
    }

    private fun initUI() {
        //mostramos datos del usuario
        mostrarDatos()

        //actualizar datoas
        binding.btnActualizarAdminPers.setOnClickListener{ actualizarPersonal() }

        //restablecer contraseña a contraseña por defecto
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
                val acceso = snapshot.child(AppUtils.DatabaseKeys.ACCESO_DB_CONST).getValue(String::class.java) ?: "No registrado"
                val telefono = snapshot.child(AppUtils.DatabaseKeys.TELEFONO_DB_CONST).getValue(String::class.java) ?: "No disponible"

                //Asignamos valores recuparados a los textviews
                binding.etNombre.setText(nombre)
                binding.etRegistro.setText(user)
                val pos1 = opcionesAcceso.indexOf(acceso)
                if (pos1 >= 0) {
                    binding.spAcceso.setSelection(pos1)
                }
                binding.etTelefono.setText(telefono)

                //BOTON PARA ELIMINAR EL REGISTRO ACTUAL (solo admin)
                binding.btnEliminarAdminPers.setOnClickListener {
                    AlertDialog.Builder(it.context)
                        .setTitle("¿Estás seguro?")
                        .setMessage("¿Deseas eliminar este usuario PERMANENTEMENTE?")
                        .setPositiveButton("Continuar") { dialog, _ ->
                            eliminarServicioSocial(user)
                            dialog.dismiss()
                        }
                        .setNegativeButton("Regresar") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            } else {
                Toast.makeText(this, "Personal no encontrado", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarPersonal() {
        val nombre = binding.etNombre.text.toString().trim()
        val registro = binding.etRegistro.text.toString().trim()
        val acceso = binding.spAcceso.selectedItem.toString()
        val telefono = binding.etTelefono.text.toString().trim()

        if (nombre.isEmpty() || registro.isEmpty() || acceso.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(user).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //si se cambio el campo "registro"
                if (registro != user) {
                    database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro)
                        .get().addOnSuccessListener { newSnapshot ->
                            //si el registro ya esta siendo utilizado
                            if (snapshot.exists()) {
                                Toast.makeText(this@EditarAdminPersActivity, "Ya existe un usuario con ese registro. Elimínalo si deseas continuar", Toast.LENGTH_SHORT).show()
                            //si el registro esta libre
                            } else {
                                if (snapshot.exists()) {
                                    val personal = AdminPers(registro, acceso, nombre, password = snapshot.child(AppUtils.DatabaseKeys.PASSWORD_DB_CONST).value.toString(), telefono)
                                    database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro).setValue(personal)
                                        .addOnSuccessListener {
                                            val registro_viejo = snapshot.child(AppUtils.DatabaseKeys.REGISTRO_DB_CONST).getValue(String::class.java) ?: AppUtils.StringKeys.ERROR_CONST
                                            eliminarServicioSocial(registro_viejo)//se elimina al viejo usuario (con el registro viejo) de la base de datos

                                            Toast.makeText(this@EditarAdminPersActivity, "Personal actualizado correctamente", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this@EditarAdminPersActivity, "Error al actualizar al personal", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(this@EditarAdminPersActivity, "No existe este registro", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                //si NO se cambio el campo "registro"
                } else {
                    if (snapshot.exists()) {
                        // Si el registro ya existe
                        val personal = AdminPers(registro, acceso/* = snapshot.child(AppUtils.DatabaseKeys.ACCESO_DB_CONST).value.toString()*/, nombre, password = snapshot.child(AppUtils.DatabaseKeys.PASSWORD_DB_CONST).value.toString(), telefono)
                        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro).setValue(personal)
                            .addOnSuccessListener {
                                Toast.makeText(this@EditarAdminPersActivity, "Personal actualizado correctamente", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@EditarAdminPersActivity, "Error al actualizar al personal", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Si el registro no existe
                        Toast.makeText(this@EditarAdminPersActivity, "No existe este registro", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditarAdminPersActivity, "Error al verificar el registro", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun eliminarServicioSocial(registro: String) {
        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario de servicio social eliminado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Error al eliminar: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun regresar() {
        finish()
    }
}