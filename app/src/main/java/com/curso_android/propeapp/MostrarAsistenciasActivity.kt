package com.curso_android.propeapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.curso_android.propeapp.databinding.ActivityMostrarAsistenciasBinding
import com.google.firebase.database.*

private lateinit var database: DatabaseReference

private lateinit var binding: ActivityMostrarAsistenciasBinding

private lateinit var registro: String

private val listaAsistencias = mutableListOf<Asistencia>()

class MostrarAsistenciasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMostrarAsistenciasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        database = AppUtils.database

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        registro = intent.getStringExtra(AppUtils.DatabaseKeys.REGISTRO_DB_CONST) ?: AppUtils.StringKeys.ERROR_CONST

        initUI()
    }

    private fun initUI(){
        cargarAsistencias(registro)

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun cargarAsistencias(registro: String) {
        if (registro != AppUtils.StringKeys.ERROR_CONST) {
            val estudiante = database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro)

            // Obtener el nombre del estudiante
            estudiante.child(AppUtils.DatabaseKeys.NOMBRE_DB_CONST).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombre = snapshot.getValue(String::class.java) ?: "Desconocido"
                    binding.tvNombre.text = nombre
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MostrarAsistenciasActivity,
                        "Error al obtener nombre: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })

            database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(registro).child(AppUtils.DatabaseKeys.ASISTENCIAS_DB_CONST).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaAsistencias.clear()
                    for (asistencia in snapshot.children) {
                        val valor = asistencia.getValue(String::class.java) ?: continue
                        listaAsistencias.add(Asistencia(valor))
                    }
                    updateRecyclerView(listaAsistencias)

                    binding.tvRegistro.text = registro
                    //binding.tvNombre.text = nombre//ES AQUÍ DONDE QUIERO MOSTRAR EL NOMBRE
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MostrarAsistenciasActivity,
                        "Error al cargar: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Error al recuperar el registro del estudiante", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateRecyclerView(results: List<Asistencia>) {
        val adapter = AsistenciasAdapter(results)
        binding.rvResultados.layoutManager = LinearLayoutManager(this)
        binding.rvResultados.adapter = adapter
    }

    private fun regresar(){
        finish()
    }
}