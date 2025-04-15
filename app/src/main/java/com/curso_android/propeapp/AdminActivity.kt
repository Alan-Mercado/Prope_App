package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

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
        //botones
        binding.cvBuscarAlumno.setOnClickListener { navegarBuscarAlumno() }
        binding.cvGrupos.setOnClickListener { navegarGrupos() }
        binding.cvAgregarAlumno.setOnClickListener { navegarAgregarAlumno() }
        binding.cvEditarAlumno.setOnClickListener { navegarEditarAlumno() }

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

    private fun navegarAgregarAlumno() {
        val intent = Intent(this, AgregarAlumActivity::class.java)
        startActivity(intent)
    }

    private fun navegarEditarAlumno() {
        val intent = Intent(this, BuscarAlumActivity::class.java)
        intent.putExtra(AppUtils.StringKeys.BOTON_CONST, AppUtils.StringKeys.EDITAR_CONST)
        startActivity(intent)
    }

    private fun mostrarBotonesAdmin() {
        //SI EL USUARIO ES ADMIN, QUE PUEDA VER LOS BOTONES EXTRA, SINO QUE ESTEN INVISIBLES

        //CAMBIAR NOMBRE DE "AdminActivity.kt" a "AdminPers.kt"???
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