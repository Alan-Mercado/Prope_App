package com.curso_android.propeapp

data class AdminPers(
    val registro: String = "",
    val acceso: String = AppUtils.StringKeys.PERSONAL_CONST,
    val nombre: String = "",
    val password: String = AppUtils.StringKeys.PASS_PREDETERMINADA_CONST,
    val telefono: String = "",
)