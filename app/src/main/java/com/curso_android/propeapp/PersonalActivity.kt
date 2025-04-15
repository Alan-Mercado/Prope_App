package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityPersonalBinding

class PersonalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

    private fun regresar(){
        finish()
    }
}