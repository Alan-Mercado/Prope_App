package com.curso_android.propeapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.curso_android.propeapp.databinding.ActivityCambiarContraseniaBinding
import com.google.firebase.database.*

class CambiarContraseniaActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityCambiarContraseniaBinding

    private lateinit var user: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCambiarContraseniaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        database = AppUtils.database

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = intent.getStringExtra(AppUtils.DatabaseKeys.REGISTRO_DB_CONST) ?: AppUtils.StringKeys.ERROR_CONST

        initUI()
    }

    private fun initUI() {
        binding.btnCambiarContrasenia.setOnClickListener {
            AlertDialog.Builder(it.context)
                .setTitle("¿Cambiar Contraseña?")
                .setMessage("¿Estás seguro de que deseas actualizar tu CONTRASEÑA?")
                .setPositiveButton("Continuar") { dialog, _ ->
                    cambiarPassword(user)
                    dialog.dismiss()
                }
                .setNegativeButton("Regresar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    private fun cambiarPassword(registro: String) {
        val contraseniaActualInput = binding.etContraseniaActual.text.toString().trim()
        val nuevaContraseniaInput = binding.etContraseniaNueva.text.toString().trim()
        val confirmarNuevaContraseniaInput = binding.etContraseniaNuevaRepetir.text.toString().trim()

        if (contraseniaActualInput.isEmpty() || nuevaContraseniaInput.isEmpty() || confirmarNuevaContraseniaInput.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        if (nuevaContraseniaInput != confirmarNuevaContraseniaInput) {
            Toast.makeText(this, "La nueva contraseña no coincide con la confirmación.",Toast.LENGTH_SHORT).show()
            return
        }

        // Acceder a la contraseña actual en la base de datos
        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST)
            .child(registro)
            .child(AppUtils.DatabaseKeys.PASSWORD_DB_CONST)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val contraseniaActualFirebase = snapshot.getValue(String::class.java)

                    if (contraseniaActualFirebase == null) {
                        Toast.makeText(this@CambiarContraseniaActivity, "Error al obtener tu contraseña actual.",Toast.LENGTH_SHORT).show()
                        return
                    }

                    // Aquí deberías aplicar el mismo hash a la contraseña actual escrita
                    val contraseniaActualInputHasheada = AppUtils.hashSHA256(contraseniaActualInput)

                    if (contraseniaActualFirebase != contraseniaActualInputHasheada) {
                        Toast.makeText(this@CambiarContraseniaActivity, "La contraseña actual es incorrecta.", Toast.LENGTH_SHORT).show()
                        return
                    }

                    // Ahora actualizamos la contraseña
                    val nuevaContraseniaHasheada = AppUtils.hashSHA256(nuevaContraseniaInput)

                    database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST)
                        .child(registro)
                        .child(AppUtils.DatabaseKeys.PASSWORD_DB_CONST)
                        .setValue(nuevaContraseniaHasheada)
                        .addOnSuccessListener {
                            Toast.makeText(this@CambiarContraseniaActivity, "Contraseña actualizada exitosamente.", Toast.LENGTH_SHORT).show()
                            navegarLogin()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@CambiarContraseniaActivity, "Error al actualizar la contraseña.", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@CambiarContraseniaActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun navegarLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun regresar(){
        finish()
    }
}