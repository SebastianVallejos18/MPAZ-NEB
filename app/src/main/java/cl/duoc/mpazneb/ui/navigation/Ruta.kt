package cl.duoc.mpazneb.ui.navigation

import cl.duoc.mpazneb.model.Perfil

sealed class Ruta(val ruta: String) {
    data object Login : Ruta("login")
    data object InicioAdmin : Ruta("inicio_admin")
    data object InicioAsistente : Ruta("inicio_asistente")
    data object InicioEstudiante : Ruta("inicio_estudiante")
}

// Decide a qué pantalla va cada perfil después de ingresar
fun rutaInicioDe(perfil: Perfil): String = when (perfil) {
    Perfil.Administrador -> Ruta.InicioAdmin.ruta
    Perfil.Asistente -> Ruta.InicioAsistente.ruta
    Perfil.Estudiante -> Ruta.InicioEstudiante.ruta
}