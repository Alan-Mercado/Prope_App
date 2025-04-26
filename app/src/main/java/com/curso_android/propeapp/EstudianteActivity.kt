package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityEstudianteBinding

class EstudianteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEstudianteBinding

    private lateinit var user: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEstudianteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = intent.getStringExtra(AppUtils.StringKeys.ESTUDIANTE_CONST) ?: AppUtils.StringKeys.ERROR_CONST

        initUI()
    }

    private fun initUI() {
        //botones
        binding.cvInformacion.setOnClickListener { navegarInformacion() }

        binding.cvQR.setOnClickListener { navegarQR() }

        binding.cvCambiarContrasenia.setOnClickListener { navegarCambiarContrasenia(user) }

        //Log.d("usuario-2", user)

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun navegarInformacion() {
        val intent = Intent(this, InfoAlumActivity::class.java)
        intent.putExtra(AppUtils.StringKeys.ESTUDIANTE_CONST, user)
        startActivity(intent)
    }

    private fun navegarQR() {
        val intent = Intent(this, MostrarQRAlumActivity::class.java)
        intent.putExtra(AppUtils.StringKeys.ESTUDIANTE_CONST, user)
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