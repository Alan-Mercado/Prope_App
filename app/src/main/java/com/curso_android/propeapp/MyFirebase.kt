package com.curso_android.propeapp

import android.app.Application
import com.google.firebase.FirebaseApp

//Clase que conecta la app con la base de datos en Firebase
class MyFirebase : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}