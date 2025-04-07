package com.curso_android.propeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.curso_android.propeapp.databinding.ActivityLoginBinding
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {
    /*********************PROXIMAS ACTIVIDADES A REALIZAR********************
     * VER LO DE LOGIN
     *  -NIVEL DE ACCESO SUPER-ADMIN??? -> usuario, personal, admin
     *
     * (PROBAR) DEFINIR CONSTANTE PARA NOMBRE USUARIO Y ASI SE COMPARTA EN TODOS LOS ACTIVITYS
     *
     *
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

        //database = FirebaseDatabase.getInstance().reference//Inicializar referencia a BD
        database = AppUtils.database

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
        //ingresar
        binding.btnIngresar.setOnClickListener {
            navegarAdminUsuario(
                binding.etRegistro.text.toString(),
                binding.etContrasenia.text.toString()
            )
        }

        //regresar
        binding.toolbarExterna.ivRegresar.setOnClickListener { regresar() }
    }

    //Funcion para comprobar usuario y redirigir a su respectiva vista
    private fun navegarAdminUsuario(user: String, pass: String) {
        val hashedPassword = AppUtils.hashSHA256(pass)//contraseña escrita en la app y con un hash
//Log.d("contrasen", hashedPassword)//MENSAJE DE PRUEBAS DE PROGRAMADOR

        database.child(AppUtils.DatabaseKeys.USUARIOS_DB_CONST).child(user)
            .get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val userAdmin = snapshot.child(AppUtils.DatabaseKeys.ADMIN_DB_CONST).value as? Boolean//guardamos el valor de "admin"
                    val userPassword = snapshot.child(AppUtils.DatabaseKeys.PASSWORD_DB_CONST).value.toString()//guardamos la contraseña de la BD

                    //verificar si la contraseña es correcta
                    if (userPassword == hashedPassword) {
                        //si es admin
                        if (userAdmin == true) {
                            Toast.makeText(this, "Inicio de sesión exitoso como administrador", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, AdminActivity::class.java)

                            //*************ENVIAR USUARIO SI ES ADMIN????****************

                            startActivity(intent)
                        //si es usuario
                        } else {
                            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, UsuarioActivity::class.java)
                            intent.putExtra(AppUtils.StringKeys.USUARIO_CONST, user)
                            //Log.d("usuario-1", user)
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