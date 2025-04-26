package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.curso_android.propeapp.databinding.ActivityMostrarGrupoBinding
import com.google.firebase.database.*

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

        //obtener el grupo desde el intent (ej: "G3")
        val grupoBuscado = intent.getStringExtra(AppUtils.StringKeys.GRUPO_CONST) ?: return

        obtenerEstudiantesPorGrupo(grupoBuscado)

        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun obtenerEstudiantesPorGrupo(grupo: String) {
        estudianteList.clear()//limpiamos la lista mostrada antes de ingresar a otro grupo
        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val usuario = userSnapshot.getValue(Estudiante::class.java)

                        //si el numero de registro o el nombre coinciden con la busqueda
                        if (usuario != null && usuario.acceso == AppUtils.StringKeys.ESTUDIANTE_CONST) {
                            if (usuario.grupo.contains(grupo)) {
                                estudianteList.add(usuario)
                            }
                        }
                    }
                    binding.tvTitulo.text = "Grupo " + grupo//mostramos el grupo en pantalla

                    updateRecyclerView(estudianteList)//actualiza la lista
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MostrarGrupoActivity, "Error al conectar con la base de datos. ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRecyclerView(results: List<Estudiante>) {
        val adapter = EstudianteAdapter(results){navegarInfoEditEstud(it, AppUtils.StringKeys.BUSCAR_CONST)}
        binding.rvResultados.layoutManager = LinearLayoutManager(this)
        binding.rvResultados.adapter = adapter
    }

    private fun navegarInfoEditEstud(registro:String, donde_ir:String) {
        //navegar a Mostrar Info Estudiante
        if (donde_ir == AppUtils.StringKeys.BUSCAR_CONST) {
            val intent = Intent(this, InfoEstudActivity::class.java)
            intent.putExtra(AppUtils.StringKeys.ESTUDIANTE_CONST, registro)
            startActivity(intent)
        }
    }

    private fun regresar() {
        finish()
    }
}