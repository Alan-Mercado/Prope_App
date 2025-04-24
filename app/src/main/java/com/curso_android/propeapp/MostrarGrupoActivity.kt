package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.curso_android.propeapp.databinding.ActivityMostrarGrupoBinding
import com.google.firebase.database.*
import java.util.Locale

private lateinit var database: DatabaseReference

private lateinit var binding: ActivityMostrarGrupoBinding

private val estudianteList = mutableListOf<Estudiante>()

class MostrarGrupoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMostrarGrupoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        database = AppUtils.database

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el grupo desde el intent (ej: "G3")
        val grupoBuscado = intent.getStringExtra(AppUtils.StringKeys.GRUPO_CONST) ?: return

        obtenerAlumnosPorGrupo(grupoBuscado)

        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun obtenerAlumnosPorGrupo(grupo: String) {
        estudianteList.clear()//limpiamos la lista mostrada de alumnos antes de ingresar a otro grupo
        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val usuario = userSnapshot.getValue(Estudiante::class.java)

                        // Si el número de registro o el nombre coinciden con la búsqueda
                        if (usuario != null && usuario.acceso == AppUtils.StringKeys.ESTUDIANTE_CONST) {
                            if (usuario.grupo.contains(grupo)) {
                                estudianteList.add(usuario)
                            }
                        }
                    }

                    // Actualiza el RecyclerView con los resultados encontrados
                    updateRecyclerView(estudianteList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de error
            }
        })
    }

    private fun updateRecyclerView(results: List<Estudiante>) {
        val adapter = AlumnoAdapter(results){navegarInfoEditAlum(it, AppUtils.StringKeys.BUSCAR_CONST)}
        binding.rvResultados.layoutManager = LinearLayoutManager(this)
        binding.rvResultados.adapter = adapter
    }

    private fun navegarInfoEditAlum(registro:String, donde_ir:String) {
        //navegar a Mostrar Info Estudiante
        if (donde_ir == AppUtils.StringKeys.BUSCAR_CONST) {
            val intent = Intent(this, InfoAlumActivity::class.java)
            intent.putExtra(AppUtils.StringKeys.ESTUDIANTE_CONST, registro)
            startActivity(intent)
        }
    }

    private fun regresar() {
        finish()
    }
}