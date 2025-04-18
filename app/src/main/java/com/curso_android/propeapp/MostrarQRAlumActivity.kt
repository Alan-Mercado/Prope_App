package com.curso_android.propeapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityMostrarQralumBinding

class MostrarQRAlumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMostrarQralumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMostrarQralumBinding.inflate(layoutInflater)
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
        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun regresar() {
        finish()
    }
}