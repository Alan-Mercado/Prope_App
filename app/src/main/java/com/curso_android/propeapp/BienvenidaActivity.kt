package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityBienvenidaBinding

class BienvenidaActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBienvenidaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBienvenidaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

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
        val intent = Intent(this, EscanearQRActivity::class.java)
        intent.putExtra(AppUtils.StringKeys.NIVEL_ACCESO_CONST,
            AppUtils.StringKeys.SERVICIO_SOCIAL_CONST)//le concedemos el nivel de acceso de servicio_social
        intent.putExtra(AppUtils.StringKeys.BOTON_CONST, AppUtils.StringKeys.BUSCAR_CONST)
        startActivity(intent)
    }
}