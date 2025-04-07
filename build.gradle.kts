// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

buildscript { // 🔹 Agregar esta sección
    dependencies {
        classpath("com.google.gms:google-services:4.3.15") // 🔹 Mover aquí el classpath
    }
}