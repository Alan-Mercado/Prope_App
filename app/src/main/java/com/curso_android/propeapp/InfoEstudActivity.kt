package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityInfoEstudBinding
import com.google.firebase.database.*

class InfoEstudActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityInfoEstudBinding

    private lateinit var user: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityInfoEstudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppUtils.database

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = intent.getStringExtra(AppUtils.StringKeys.ESTUDIANTE_CONST) ?: AppUtils.StringKeys.ERROR_CONST

        initUI()
    }

    private fun initUI() {
        //cargamos los datos del estudiante
        mostrarDatos()

        //asistencias
        binding.btnAsistencias.setOnClickListener { navegarAsistencias(user) }

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

                //Asignamos valores recuparados a los textviews
                binding.tvNombre.text = nombre
                binding.tvRegistro.text = user
                binding.tvEstatus.text = estatus
                binding.tvGrupo.text = grupo
                binding.tvContacto.text = contacto
                binding.tvTelefono1.text = tel_1
                //binding.tvTutor2.text = tutor_2
                //binding.tvTelefono2.text = tel_2
                binding.tvCredencial.text = cred_entr
            } else {
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show()
        }
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