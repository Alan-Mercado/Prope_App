package com.curso_android.propeapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityLoginBinding
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {
    /*********************PROXIMAS ACTIVIDADES A REALIZAR********************
     *
     *
     *
     * */

    private lateinit var database: DatabaseReference//Referencia a Firebase

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.dorado_color, theme)

        database = AppUtils.database//Inicializar referencia a BD

        /*setSupportActionBar(binding.toolBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)//Agrega botón de retroceso si es necesario
        }*/

        ViewCompat.setOnApplyWindowInsetsListener(binding.root/*main*/) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
    }

    private fun initUI() {
        //mensaje recuperar contraseña
        binding.tvOlvidasteContrasenia.setOnClickListener {
            AlertDialog.Builder(it.context)
                .setTitle("Restablecer Contraseña")
                .setMessage("Para restablecer tu contraseña, acude con Coordinador(a) del curso y solicítalo.").show()
        }

        //ingresar
        binding.btnIngresar.setOnClickListener {
            navegarVistaUsuario(
                binding.etRegistro.text.toString(),
                binding.etContrasenia.text.toString()
            )
        }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }

//Log.d("conttemp", AppUtils.hashSHA256("a"))//FUNCION PARA GENERAR CONTRASEÑAS Y COPIARLAS DEL LOGCAT
    }

    //Funcion para comprobar usuario y redirigir a su respectiva vista
    private fun navegarVistaUsuario(user: String, pass: String) {
        val hashedPassword = AppUtils.hashSHA256(pass)//contraseña escrita en la app y con un hash

        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(user)
            .get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val nivelAcceso = snapshot.child(AppUtils.DatabaseKeys.ACCESO_DB_CONST).value as? String//guardamos el valor de "admin"
                    val userPassword = snapshot.child(AppUtils.DatabaseKeys.PASSWORD_DB_CONST).value.toString()//guardamos la contraseña de la BD

                    //verificar si la contraseña es correcta
                    if (userPassword == hashedPassword) {
                        //si es admin
                        if (nivelAcceso == AppUtils.StringKeys.ADMIN_CONST) {
                            Toast.makeText(this, "Inicio de sesión exitoso como administrador",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, AdminPersActivity::class.java)
                            intent.putExtra(AppUtils.StringKeys.NIVEL_ACCESO_CONST, AppUtils.StringKeys.ADMIN_CONST)
                            intent.putExtra(AppUtils.DatabaseKeys.REGISTRO_DB_CONST, user)
                            startActivity(intent)

                        //si es personal (guardia/chic@ servicio)
                        } else if (nivelAcceso == AppUtils.StringKeys.SERVICIO_SOCIAL_CONST) {
                            Toast.makeText(this, "Inicio de sesión exitoso como servicio social", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, AdminPersActivity::class.java)
                            intent.putExtra(AppUtils.StringKeys.NIVEL_ACCESO_CONST, AppUtils.StringKeys.SERVICIO_SOCIAL_CONST)
                            intent.putExtra(AppUtils.DatabaseKeys.REGISTRO_DB_CONST, user)
                            startActivity(intent)

                        //si es estudiante
                        } else if (nivelAcceso == AppUtils.StringKeys.ESTUDIANTE_CONST) {
                            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, EstudianteActivity::class.java)
                            intent.putExtra(AppUtils.StringKeys.ESTUDIANTE_CONST, user)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
    }

    private fun regresar(){
        /*val intent = Intent(this, BienvenidaActivity::class.java)
        startActivity(intent)*/
        finish()
    }
}