package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.curso_android.propeapp.databinding.ActivityBuscarAdminPersBinding
import com.google.firebase.database.*
import java.util.Locale

class BuscarAdminPersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuscarAdminPersBinding

    private lateinit var database: DatabaseReference

    private lateinit var destino: String

    private lateinit var personalAdapter: AdminPersAdapter
    private var personalList = mutableListOf<AdminPers>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBuscarAdminPersBinding.inflate(layoutInflater)
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
        // Configuración del RecyclerView
        personalAdapter = AdminPersAdapter(personalList){navegarEditAdminPers(it, destino)}//*******************VER SI ES NECESARIO LO DE "DESTINO"****************
        binding.rvResultados.layoutManager = LinearLayoutManager(this)
        binding.rvResultados.adapter = personalAdapter

        // Configurar el SearchView
        binding.svBuscador.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    buscarPersonal(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    buscarPersonal(it)
                }
                return true
            }
        })

        binding.btnBuscar.setOnClickListener {
            val query = binding.svBuscador.query.toString()
            buscarPersonal(query)
        }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun buscarPersonal(query: String) {
        val queryLower = query.toLowerCase(Locale.getDefault()) // Para hacer la búsqueda insensible a mayúsculas/minúsculas
        val results = mutableListOf<AdminPers>()

        // Buscar en Firebase
        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d("probamos", "si existe la snapshot")
                    for (userSnapshot in snapshot.children) {
                        Log.d("probamos", "si entra al for-in")
                        val usuario = userSnapshot.getValue(AdminPers::class.java)
                        //val esAdmin = userSnapshot.getValue(Alumno::class.java)

                        // Si el número de registro o el nombre coinciden con la búsqueda
                        if (usuario != null && usuario.acceso != AppUtils.StringKeys.ESTUDIANTE_CONST) {
                        //if (usuario != null && (usuario.acceso == AppUtils.StringKeys.PERSONAL_CONST || usuario.acceso == AppUtils.StringKeys.ADMIN_CONST)) {
                            Log.d("probamos", "si existe el usuario")
                            if (usuario.nombre.toLowerCase(Locale.getDefault()).contains(queryLower) ||
                                userSnapshot.key?.contains(queryLower) == true/* && (usuario.acceso == AppUtils.StringKeys.PERSONAL_CONST || usuario.acceso == AppUtils.StringKeys.ADMIN_CONST)*/) {
                                Log.d("probamos", "si añade el usuario")
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

    private fun updateRecyclerView(results: List<AdminPers>) {
        //val adapter = AlumnoAdapter(results)
        val adapter = AdminPersAdapter(results){navegarEditAdminPers(it, destino)}
        binding.rvResultados.layoutManager = LinearLayoutManager(this)
        binding.rvResultados.adapter = adapter
    }

    private fun navegarEditAdminPers(registro:String, donde_ir:String) {
        //navegar a Buscar Personal
        if (donde_ir == AppUtils.StringKeys.EDITAR_PERSONAL_CONST) {
            val intent = Intent(this, EditarAdminPersActivity::class.java)
            intent.putExtra(AppUtils.StringKeys.PERSONAL_CONST, registro)
            startActivity(intent)
        }
    }

    private fun regresar() {
        finish()
    }
}