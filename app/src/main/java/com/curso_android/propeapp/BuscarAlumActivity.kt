package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.curso_android.propeapp.databinding.ActivityBuscarAlumBinding
import com.google.firebase.database.*
import java.util.Locale

class BuscarAlumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuscarAlumBinding

    private lateinit var database: DatabaseReference

    private lateinit var destino: String


    private lateinit var alumnoAdapter: AlumnoAdapter
    private var alumnoList = mutableListOf<Alumno>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBuscarAlumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = AppUtils.database

        destino = intent.getStringExtra(AppUtils.StringKeys.BOTON_CONST) ?: AppUtils.StringKeys.ERROR_CONST

        initUI()
    }

    private fun initUI() {
        //botones
        //binding.btnBuscar.setOnClickListener { navegarInfoAlum() }

        // Configuración del RecyclerView
        alumnoAdapter = AlumnoAdapter(alumnoList){navegarInfoEditAlum(it, destino)}
        //alumnoAdapter = AlumnoAdapter{navegarInfoAlum(it)}
        binding.rvResultados.layoutManager = LinearLayoutManager(this)
        binding.rvResultados.adapter = alumnoAdapter

        // Configurar el SearchView
        binding.svBuscador.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    buscarAlumnos(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    buscarAlumnos(it)
                }
                return true
            }
        })

        binding.btnBuscar.setOnClickListener {
            val query = binding.svBuscador.query.toString()
            buscarAlumnos(query)
        }

        //escanear QR
        binding.btnEscanearQR.setOnClickListener { navegarEscanearQR() }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun buscarAlumnos(query: String) {
        val queryLower = query.toLowerCase(Locale.getDefault()) // Para hacer la búsqueda insensible a mayúsculas/minúsculas
        val results = mutableListOf<Alumno>()

        // Buscar en Firebase
        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val usuario = userSnapshot.getValue(Alumno::class.java)
                        //val esAdmin = userSnapshot.getValue(Alumno::class.java)

                        // Si el número de registro o el nombre coinciden con la búsqueda
                        if (usuario != null && usuario.acceso == AppUtils.StringKeys.ESTUDIANTE_CONST) {
                            if (usuario.nombre.toLowerCase(Locale.getDefault()).contains(queryLower) ||
                                userSnapshot.key?.contains(queryLower) == true) {
                                results.add(usuario)
                            }
                        }
                    }

                    // Actualiza el RecyclerView con los resultados encontrados
                    updateRecyclerView(results)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error si ocurre
            }
        })
    }

    private fun updateRecyclerView(results: List<Alumno>) {
        //val adapter = AlumnoAdapter(results)
        val adapter = AlumnoAdapter(results){navegarInfoEditAlum(it, destino)}
        binding.rvResultados.layoutManager = LinearLayoutManager(this)
        binding.rvResultados.adapter = adapter
    }

    private fun navegarEscanearQR() {
        val intent = Intent(this, EscanearQRActivity::class.java)
        startActivity(intent)
    }

    private fun navegarInfoEditAlum(registro:String, donde_ir:String) {
        if (donde_ir == AppUtils.StringKeys.EDITAR_CONST){
            val intent = Intent(this, EditarAlumActivity::class.java)
            intent.putExtra(AppUtils.StringKeys.ESTUDIANTE_CONST, registro)
            startActivity(intent)
        } else if (donde_ir == AppUtils.StringKeys.BUSCAR_CONST) {
            val intent = Intent(this, InfoAlumActivity::class.java)
            intent.putExtra(AppUtils.StringKeys.ESTUDIANTE_CONST, registro)
            startActivity(intent)
        }
    }

    private fun regresar() {
        finish()
    }
}