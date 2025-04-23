package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivitySeleccionarGrupoBinding
import com.google.firebase.database.*
import com.google.firebase.database.ValueEventListener

class SeleccionarGrupoActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivitySeleccionarGrupoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySeleccionarGrupoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        database = AppUtils.database

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
    }

    private fun initUI() {
        mostrarGrupos()

        binding.cvGrupo1.setOnClickListener { navegarGrupo("G1") }
        binding.cvGrupo2.setOnClickListener { navegarGrupo("G2") }
        binding.cvGrupo3.setOnClickListener { navegarGrupo("G3") }
        binding.cvGrupo4.setOnClickListener { navegarGrupo("G4") }
        binding.cvGrupo5.setOnClickListener { navegarGrupo("G5") }
        binding.cvGrupo6.setOnClickListener { navegarGrupo("G6") }
        binding.cvGrupo7.setOnClickListener { navegarGrupo("G7") }
        binding.cvGrupo8.setOnClickListener { navegarGrupo("G8") }
        binding.cvGrupo9.setOnClickListener { navegarGrupo("G9") }
        binding.cvGrupo10.setOnClickListener { navegarGrupo("G10") }
        binding.cvGrupo11.setOnClickListener { navegarGrupo("G11") }
        binding.cvGrupo12.setOnClickListener { navegarGrupo("G12") }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun mostrarGrupos() {
        val usuariosRef = database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST)
        val gruposVisibles = mutableSetOf<String>()

        usuariosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (usuarioSnapshot in snapshot.children) {
                    val grupo = usuarioSnapshot.child(AppUtils.DatabaseKeys.GRUPO_DB_CONST).getValue(String::class.java)
                    if (!grupo.isNullOrBlank()) {
                        gruposVisibles.add(grupo)
                    }
                }

                for (grupoId in gruposVisibles) {
                    when (grupoId) {
                        "G1" -> binding.cvGrupo1.visibility = View.VISIBLE
                        "G2" -> binding.cvGrupo2.visibility = View.VISIBLE
                        "G3" -> binding.cvGrupo3.visibility = View.VISIBLE
                        "G4" -> binding.cvGrupo4.visibility = View.VISIBLE
                        "G5" -> binding.cvGrupo5.visibility = View.VISIBLE
                        "G6" -> binding.cvGrupo6.visibility = View.VISIBLE
                        "G7" -> binding.cvGrupo7.visibility = View.VISIBLE
                        "G8" -> binding.cvGrupo8.visibility = View.VISIBLE
                        "G9" -> binding.cvGrupo9.visibility = View.VISIBLE
                        "G10" -> binding.cvGrupo10.visibility = View.VISIBLE
                        "G11" -> binding.cvGrupo11.visibility = View.VISIBLE
                        "G12" -> binding.cvGrupo12.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SeleccionarGrupoActivity, "Error al cargar los grupos disponibles", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navegarGrupo(grupo: String){
        val intent = Intent(this, MostrarGrupoActivity::class.java)
        intent.putExtra(AppUtils.StringKeys.GRUPO_CONST, grupo)
        startActivity(intent)
    }

    private fun regresar() {
        finish()
    }
}