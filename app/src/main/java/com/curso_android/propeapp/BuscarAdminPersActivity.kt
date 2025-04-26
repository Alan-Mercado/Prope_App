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
import com.curso_android.propeapp.databinding.ActivityBuscarAdminPersBinding
import com.google.firebase.database.*
import java.util.Locale

class BuscarAdminPersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuscarAdminPersBinding

    private lateinit var database: DatabaseReference

    private lateinit var destino: String

    private lateinit var admin_persAdapter: AdminPersAdapter
    private var admin_persList = mutableListOf<AdminPers>()

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
        //configurar recyclerview
        admin_persAdapter = AdminPersAdapter(admin_persList){navegarEditAdminPers(it, destino)}
        binding.rvResultados.layoutManager = LinearLayoutManager(this)
        binding.rvResultados.adapter = admin_persAdapter

        //configurar searchview (lista de usuarios)
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
        val queryLower = query.toLowerCase(Locale.getDefault())//modificamos la busqueda para hacerla siempre en minusculas
        val results = mutableListOf<AdminPers>()

        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val usuario = userSnapshot.getValue(AdminPers::class.java)

                        //si el registro o el nombre coinciden con lp buscado
                        if (usuario != null && usuario.acceso != AppUtils.StringKeys.ESTUDIANTE_CONST) {
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
                Toast.makeText(this@BuscarAdminPersActivity, "Error al conectar con la base de datos. ${error.message}", Toast.LENGTH_SHORT).show()
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
        if (donde_ir == AppUtils.StringKeys.EDITAR_SERVICIO_SOCIAL_CONST) {
            val intent = Intent(this, EditarAdminPersActivity::class.java)
            intent.putExtra(AppUtils.StringKeys.SERVICIO_SOCIAL_CONST, registro)
            startActivity(intent)
        }
    }

    private fun regresar() {
        finish()
    }
}