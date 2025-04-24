package com.curso_android.propeapp

data class Estudiante(
    val registro: String = "",
    val acceso: String = AppUtils.StringKeys.ESTUDIANTE_CONST,
    val nombre: String = "",
    val password: String = AppUtils.StringKeys.PASS_PREDETERMINADA_CONST,
    val estatus: String = "",
    val grupo: String = "",
    val contacto: String = "",
    val tel_1: String = "",
    //val tutor_2: String ?= "No disponible",
    //val tel_2: String ?= "No disponible",
    val cred_entr: String ?= "Pendiente",

    val asistencias: MutableMap<String, String> = mutableMapOf()
)