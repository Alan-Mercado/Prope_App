package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.curso_android.propeapp.databinding.ActivityAdminPersBinding

class AdminPersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPersBinding

    private lateinit var user: String
    private lateinit var nivel_acceso: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAdminPersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = intent.getStringExtra(AppUtils.DatabaseKeys.REGISTRO_DB_CONST) ?: AppUtils.StringKeys.ERROR_CONST
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

        binding.cvAgregarAdminPers.setOnClickListener { navegarAgregarPersonal() }
        binding.cvEditarAdminPers.setOnClickListener { navegarEditarPersonal() }

        binding.cvCambiarContrasenia.setOnClickListener { navegarCambiarContrasenia(user) }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun navegarBuscarAlumno() {
        val intent = Intent(this, BuscarEstudActivity::class.java)
        intent.putExtra(AppUtils.StringKeys.BOTON_CONST, AppUtils.StringKeys.BUSCAR_CONST)
        startActivity(intent)
    }

    private fun navegarGrupos() {
        val intent = Intent(this, SeleccionarGrupoActivity::class.java)
        startActivity(intent)
    }

    private fun navegarAgregarEstudiante() {
        val intent = Intent(this, AgregarEstudActivity::class.java)
        startActivity(intent)
    }

    private fun navegarEditarEstudiante() {
        val intent = Intent(this, BuscarEstudActivity::class.java)
        intent.putExtra(AppUtils.StringKeys.BOTON_CONST, AppUtils.StringKeys.EDITAR_ESTUDIANTE_CONST)
        intent.putExtra(AppUtils.StringKeys.NIVEL_ACCESO_CONST, nivel_acceso)
        startActivity(intent)
    }

    private fun mostrarBotonesAdmin() {
        //si es admin, que vea todas las opciones ocultas
        if (nivel_acceso == AppUtils.StringKeys.ADMIN_CONST){
            binding.tvTitulo.text = "Menú ADMINISTRADOR"
            binding.tvDescripcion.text = "Aquí puedes buscar un estudiante por su nombre, revisar la lista de grupos, agregar a un estudiante o personal nuevo o editar la información de los mismos, entre otras opciones"
            binding.cvAgregarAdminPers.isVisible = true
            binding.cvEditarAdminPers.isVisible = true
        }
    }

    private fun navegarAgregarPersonal() {
        val intent = Intent(this, AgregarAdminPersActivity::class.java)
        startActivity(intent)
    }

    private fun navegarEditarPersonal() {
        val intent = Intent(this, BuscarAdminPersActivity::class.java)
        intent.putExtra(AppUtils.StringKeys.BOTON_CONST, AppUtils.StringKeys.EDITAR_SERVICIO_SOCIAL_CONST)//PONER EXTRA DE REGISTRO DEL ADMIN/PERSONAL
        startActivity(intent)
    }

    private fun navegarCambiarContrasenia(registro: String) {
        val intent = Intent(this, CambiarContraseniaActivity::class.java)
        intent.putExtra(AppUtils.DatabaseKeys.REGISTRO_DB_CONST, registro)
        startActivity(intent)
    }

    private fun regresar(){
        finish()
    }
}