package com.curso_android.propeapp

data class Alumno(
    val registro: String = "",
    val admin: Boolean = false,
    val nombre: String = "",
    val password: String = AppUtils.StringKeys.PASS_PREDETERMINADA_CONST,
    val estatus: String = "",
    val grupo: String = "",
    val tutor_1: String = "",
    val tel_1: String = "",
    val tutor_2: String ?= "No disponible",
    val tel_2: String ?= "No disponible",
    val cred_entr: String ?= "Pendiente"
) /*{
    constructor() : this(false, "", AppUtils.StringKeys.PASS_PREDETERMINADA_CONST, "", "", "", "", "", "")
}*/