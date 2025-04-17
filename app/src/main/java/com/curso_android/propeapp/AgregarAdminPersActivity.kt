package com.curso_android.propeapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityAgregarAdminPersBinding
import com.google.firebase.database.*

class AgregarAdminPersActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference//Referencia a Firebase

    private lateinit var binding: ActivityAgregarAdminPersBinding

    private lateinit var opcionesAcceso: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAgregarAdminPersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        //database = FirebaseDatabase.getInstance().reference//Inicializar referencia a BD
        database = AppUtils.database

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        opcionesAcceso = listOf(AppUtils.StringKeys.ADMIN_CONST, AppUtils.StringKeys.PERSONAL_CONST)

        //Creo que esto se puede optimizar
        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesAcceso)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spAcceso.adapter = adapter1

        initUI()
    }

    private fun initUI() {
        //agregar alum
        binding.btnAgregarAdminPers.setOnClickListener { agregarAdminPers() }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun agregarAdminPers() {
        val nombre = binding.etNombre.text.toString().trim()
        val registro = binding.etRegistro.text.toString().trim()
        val acceso = binding.spAcceso.selectedItem.toString()
        val telefono = binding.etTelefono.text.toString().trim()

        if (nombre.isEmpty() || registro.isEmpty() || acceso.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val admin_pers = AdminPers(registro, acceso, nombre, password = AppUtils.hashSHA256(AppUtils.StringKeys.PASS_PREDETERMINADA_CONST), telefono)

        // Verificar si ya existe un registro con ese ID en la base de datos
        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Si el registro ya existe
                    Toast.makeText(this@AgregarAdminPersActivity, "Ya existe este registro de personal", Toast.LENGTH_SHORT).show()
                } else {
                    // Si el registro no existe agregar el nuevo alumno
                    database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro).setValue(admin_pers)
                        .addOnSuccessListener {
                            Toast.makeText(this@AgregarAdminPersActivity, "Personal agregado correctamente", Toast.LENGTH_SHORT).show()
                            limpiarCampos()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@AgregarAdminPersActivity, "Error al agregar al personal", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AgregarAdminPersActivity, "Error al verificar el registro", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun limpiarCampos() {
        binding.etNombre.text.clear()
        binding.etRegistro.text.clear()
        binding.spAcceso.setSelection(0)
        binding.etTelefono.text.clear()
    }

    private fun regresar(){
        finish()
    }
}
