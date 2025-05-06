package com.curso_android.propeapp

import com.google.firebase.database.*
import java.security.MessageDigest

object AppUtils {
    //Referencia a Firebase Database
    val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference
    }

    //Funcion para hashear con SHA-256
    fun hashSHA256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    //Funcion para restablecer contraseña
    fun restablecerPassword(registro: String, callback: (Boolean) -> Unit) {
        val nuevaPasswordHash = hashSHA256(StringKeys.PASS_PREDETERMINADA_CONST)

        val usuario = database.child(DatabaseKeys.USUARIOS_DB_CONST).child(registro)

        usuario.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //si se encuentra el registro
                if (snapshot.exists()) {
                    usuario.child(DatabaseKeys.PASSWORD_DB_CONST).setValue(nuevaPasswordHash)
                        .addOnCompleteListener { task -> callback(task.isSuccessful) }//indicamos de manera asincrona que envie la callback

                //si no se encuentra el registro
                } else {
                    callback(false)
                }
            }

            //si ocurre un error al conectar con la base de datos
            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }


    //Constantes de nombres de claves en la base de datos
    object DatabaseKeys {
        const val USUARIOS_DB_CONST = "Usuarios"

        const val REGISTRO_DB_CONST = "registro"
        const val ACCESO_DB_CONST = "acceso"
        const val NOMBRE_DB_CONST = "nombre"
        const val PASSWORD_DB_CONST = "password"
        const val ESTATUS_DB_CONST = "estatus"
        const val GRUPO_DB_CONST = "grupo"
        const val CONTACTO_DB_CONST = "contacto"
        const val TEL_1_DB_CONST = "tel_1"
        const val TELEFONO_DB_CONST = "telefono"
        //const val TUTOR_2_DB_CONST = "tutor_2"
        //const val TEL_2_DB_CONST = "tel_2"
        const val CREDENCIAL_ENTREGADA_DB_CONST = "cred_entr"
        const val ASISTENCIAS_DB_CONST = "asistencias"
    }

    //Constantes de strings claves en el funcionamiento de la app
    object StringKeys {
        const val ESTUDIANTE_CONST = "estudiante"
        const val SERVICIO_SOCIAL_CONST = "servicio social"
        const val ADMIN_CONST = "administrador"
        const val NIVEL_ACCESO_CONST = "nivel_acceso"
        const val BOTON_CONST = "boton_presionado"
        const val EDITAR_ESTUDIANTE_CONST = "editar_estudiante"
        const val EDITAR_SERVICIO_SOCIAL_CONST = "editar_servicio_social"
        const val BUSCAR_CONST = "buscar"
        const val GRUPO_CONST = "grupo"

        const val PASS_PREDETERMINADA_CONST = "Ceti1234"
        const val ERROR_CONST = "ERROR"
    }
}