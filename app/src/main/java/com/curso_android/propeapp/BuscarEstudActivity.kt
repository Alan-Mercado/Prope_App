package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.curso_android.propeapp.databinding.ActivityBuscarEstudBinding
import com.google.firebase.database.*
import java.util.Locale

class BuscarEstudActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuscarEstudBinding

    private lateinit var database: DatabaseReference

    private lateinit var destino: String
    private lateinit var nivel_acceso: String

    private lateinit var estudianteAdapter: EstudianteAdapter
    private var estudianteList = mutableListOf<Estudiante>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBuscarEstudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = AppUtils.database

        destino = intent.getStringExtra(AppUtils.StringKeys.BOTON_CONST) ?: AppUtils.StringKeys.ERROR_CONST
        nivel_acceso = intent.getStringExtra(AppUtils.StringKeys.NIVEL_ACCESO_CONST) ?: AppUtils.StringKeys.ERROR_CONST

        initUI()
    }

    private fun initUI() {
        //configuracion del recyclervew
        estudianteAdapter = EstudianteAdapter(estudianteList){navegarInfoEditEstud(it, destino)}

        binding.rvResultados.layoutManager = LinearLayoutManager(this)
        binding.rvResultados.adapter = estudianteAdapter

        //configurar lista de resultados (searchview)
        binding.svBuscador.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    buscarEstudiantes(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    buscarEstudiantes(it)
                }
                return true
            }
        })

        //buscar estudiantes
        binding.btnBuscar.setOnClickListener {
            val query = binding.svBuscador.query.toString()
            buscarEstudiantes(query)
        }

        //escanear QR
        binding.btnEscanearQR.setOnClickListener { navegarEscanearQR() }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun buscarEstudiantes(query: String) {
        val queryLower = query.toLowerCase(Locale.getDefault())//modificamos la busqueda para hacerla siempre en minusculas
        val results = mutableListOf<Estudiante>()

        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val usuario = userSnapshot.getValue(Estudiante::class.java)

                        //si el registro o el nombre coinciden con lp buscado
                        if (usuario != null && usuario.acceso == AppUtils.StringKeys.ESTUDIANTE_CONST) {
                            if (usuario.nombre.toLowerCase(Locale.getDefault()).contains(queryLower) ||
                                userSnapshot.key?.contains(queryLower) == true) {
                                results.add(usuario)
                            }
                        }
                    }

                    updateRecyclerView(results)//actualizar el recyclerView con los resultados encontrados
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BuscarEstudActivity, "Error al conectar con la base de datos. ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRecyclerView(results: List<Estudiante>) {
        val adapter = EstudianteAdapter(results){navegarInfoEditEstud(it, destino/*, nombre*/)}
        binding.rvResultados.layoutManager = LinearLayoutManager(this)
        binding.rvResultados.adapter = adapter
    }

    private fun navegarEscanearQR() {
        val intent = Intent(this, EscanearQRActivity::class.java)
        intent.putExtra(AppUtils.StringKeys.NIVEL_ACCESO_CONST, nivel_acceso)
        intent.putExtra(AppUtils.StringKeys.BOTON_CONST, destino)
        startActivity(intent)
    }

    private fun navegarInfoEditEstud(registro:String, donde_ir:String) {
        //navegar a Mostrar Info Estudiante
        if (donde_ir == AppUtils.StringKeys.BUSCAR_CONST) {
            val intent = Intent(this, InfoEstudActivity::class.java)
            intent.putExtra(AppUtils.StringKeys.ESTUDIANTE_CONST, registro)
            startActivity(intent)

        //navegar a Editar Estudiante
        } else if (donde_ir == AppUtils.StringKeys.EDITAR_ESTUDIANTE_CONST){
            val intent = Intent(this, EditarEstudActivity::class.java)
            intent.putExtra(AppUtils.StringKeys.ESTUDIANTE_CONST, registro)
            intent.putExtra(AppUtils.StringKeys.NIVEL_ACCESO_CONST, nivel_acceso)
            startActivity(intent)
        }
    }

    private fun regresar() {
        finish()
    }
}