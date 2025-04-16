package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.curso_android.propeapp.databinding.ActivityAdminBinding

class AdminPersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    private lateinit var nivel_acceso: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nivel_acceso = intent.getStringExtra(AppUtils.StringKeys.NIVEL_ACCESO_CONST) ?: AppUtils.StringKeys.ERROR_CONST

        initUI()
    }

    private fun initUI() {
        binding.cvAgregarAdminPers.isVisible = false
        binding.cvEditarAdminPers.isVisible = false

        mostrarBotonesAdmin()

        //botones
        binding.cvBuscarAlumno.setOnClickListener { navegarBuscarAlumno() }
        binding.cvGrupos.setOnClickListener { navegarGrupos() }
        binding.cvAgregarEstudiante.setOnClickListener { navegarAgregarEstudiante() }
        binding.cvEditarEstudiante.setOnClickListener { navegarEditarEstudiante() }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun navegarBuscarAlumno() {
        val intent = Intent(this, BuscarAlumActivity::class.java)
        intent.putExtra(AppUtils.StringKeys.BOTON_CONST, AppUtils.StringKeys.BUSCAR_CONST)
        startActivity(intent)
    }

    private fun navegarGrupos() {
        val intent = Intent(this, SeleccionarGrupoActivity::class.java)
        startActivity(intent)
    }

    private fun navegarAgregarEstudiante() {
        val intent = Intent(this, AgregarAlumActivity::class.java)
        startActivity(intent)
    }

    private fun navegarEditarEstudiante() {
        val intent = Intent(this, BuscarAlumActivity::class.java)
        intent.putExtra(AppUtils.StringKeys.BOTON_CONST, AppUtils.StringKeys.EDITAR_CONST)
        startActivity(intent)
    }

    private fun mostrarBotonesAdmin() {
        //SI EL USUARIO ES ADMIN, QUE PUEDA VER LOS BOTONES EXTRA, SINO QUE ESTEN INVISIBLES

        //CAMBIAR NOMBRE DE "AdminActivity.kt" a "AdminPers.kt"???
        if (nivel_acceso == AppUtils.StringKeys.ADMIN_CONST){
            binding.cvAgregarAdminPers.isVisible = true
            binding.cvEditarAdminPers.isVisible = true
        }
    }

    /*private fun navegarAgregarPersonal() {
        val intent = Intent(this, AgregarPersActivity::class.java)
        startActivity(intent)
    }

    private fun navegarEditarPersonal() {
        val intent = Intent(this, BuscarPersActivity::class.java)
        intent.putExtra(AppUtils.StringKeys.BOTON_CONST, AppUtils.StringKeys.EDITAR_CONST)//PONER EXTRA DE REGISTRO DEL ADMIN/PERSONAL
        startActivity(intent)
    }*/

    private fun regresar(){
        finish()
    }
}