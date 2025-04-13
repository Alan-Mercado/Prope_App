package com.curso_android.propeapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityEscanearQractivityBinding
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
        checkGrupos()

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    /*private fun obtenerGruposUnicos(callback: (List<String>) -> Unit) {
        val gruposSet = mutableSetOf<String>()

        val usuariosRef = database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST)

        usuariosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (usuarioSnapshot in snapshot.children) {
                    val grupo = usuarioSnapshot.child(AppUtils.DatabaseKeys.GRUPO_DB_CONST).getValue(String::class.java)
                    if (!grupo.isNullOrBlank()) {
                        gruposSet.add(grupo)
                    }
                }
                callback(gruposSet.toList().sorted())
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList()) // Por si hay error
            }
        })
    }*/

    // Método para consultar los grupos en Firebase
    /*private fun checkGrupos() {
        val gruposRef = database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(AppUtils.DatabaseKeys.GRUPO_DB_CONST) // Suponiendo que la estructura de tu base de datos tiene una clave "grupos"

        // Escucha los cambios en los grupos
        gruposRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Recorre los grupos existentes
                for (grupoSnapshot in snapshot.children) {
                    val grupoId = grupoSnapshot.key // El ID del grupo

                    // Aquí, hacemos visible el CardView correspondiente al grupo
                    when (grupoId) {
                        "1" -> binding.cvGrupo1.visibility = View.VISIBLE
                        "2" -> binding.cvGrupo2.visibility = View.VISIBLE
                        "3" -> binding.cvGrupo3.visibility = View.VISIBLE
                        "4" -> binding.cvGrupo4.visibility = View.VISIBLE
                        "5" -> binding.cvGrupo5.visibility = View.VISIBLE
                        "6" -> binding.cvGrupo6.visibility = View.VISIBLE
                        "7" -> binding.cvGrupo7.visibility = View.VISIBLE
                        "8" -> binding.cvGrupo8.visibility = View.VISIBLE
                        "9" -> binding.cvGrupo9.visibility = View.VISIBLE
                        "10" -> binding.cvGrupo10.visibility = View.VISIBLE
                        "11" -> binding.cvGrupo11.visibility = View.VISIBLE
                        "12" -> binding.cvGrupo12.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Maneja el error si es necesario
            }
        })
    }*/
    private fun checkGrupos() {
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
                        "1" -> binding.cvGrupo1.visibility = View.VISIBLE
                        "2" -> binding.cvGrupo2.visibility = View.VISIBLE
                        "3" -> binding.cvGrupo3.visibility = View.VISIBLE
                        "4" -> binding.cvGrupo4.visibility = View.VISIBLE
                        "5" -> binding.cvGrupo5.visibility = View.VISIBLE
                        "6" -> binding.cvGrupo6.visibility = View.VISIBLE
                        "7" -> binding.cvGrupo7.visibility = View.VISIBLE
                        "8" -> binding.cvGrupo8.visibility = View.VISIBLE
                        "9" -> binding.cvGrupo9.visibility = View.VISIBLE
                        "10" -> binding.cvGrupo10.visibility = View.VISIBLE
                        "11" -> binding.cvGrupo11.visibility = View.VISIBLE
                        "12" -> binding.cvGrupo12.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Maneja el error si es necesario
            }
        })
    }
/************************************************************************************/
/********************       PONER UN SPINNER PARA LOS GRUPOS    *********************/
/************************************************************************************/

    private fun regresar() {
        finish()
    }
}