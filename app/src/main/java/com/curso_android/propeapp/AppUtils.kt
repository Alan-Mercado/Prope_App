package com.curso_android.propeapp

import com.google.firebase.database.*
import java.security.MessageDigest

object AppUtils {
    // Referencia a Firebase Database
    val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference
    }

    // Función para hashear con SHA-256
    fun hashSHA256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Constantes de nombres de claves en la base de datos
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
        const val CREDENCIAL_ENTREGADA = "cred_entr"
    }

    object StringKeys {
        const val ESTUDIANTE_CONST = "estudiante"
        const val PERSONAL_CONST = "personal"
        const val ADMIN_CONST = "administrador"
        const val NIVEL_ACCESO_CONST = "nivel_acceso"
        const val BOTON_CONST = "boton_presionado"
        const val EDITAR_ESTUDIANTE_CONST = "editar_estudiante"
        const val EDITAR_PERSONAL_CONST = "editar_personal"
        const val BUSCAR_CONST = "buscar"
        const val ERROR_CONST = "ERROR"
        const val PASS_PREDETERMINADA_CONST = "Ceti1234"
    }
}