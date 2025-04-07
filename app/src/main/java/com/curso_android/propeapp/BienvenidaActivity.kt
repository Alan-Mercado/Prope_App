package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityBienvenidaBinding
import com.google.android.material.button.MaterialButton

class BienvenidaActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBienvenidaBinding

    //private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBienvenidaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        //setSupportActionBar(binding.toolBar)
        /*supportActionBar?.apply {
            title = "Inicio de sesión"  // Título de la Toolbar
            setDisplayHomeAsUpEnabled(true)  // Agrega botón de retroceso si es necesario
        }*/

        ViewCompat.setOnApplyWindowInsetsListener(binding.root/*main*/) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
    }

    private fun initUI() {
        binding.btnIngresar.setOnClickListener { navegarLogin() }
        binding.btnEscanearQR.setOnClickListener { navegarEscanearQR() }
    }

    private fun navegarLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun navegarEscanearQR() {
        startActivity(Intent(this, EscanearQRActivity::class.java))
    }
}