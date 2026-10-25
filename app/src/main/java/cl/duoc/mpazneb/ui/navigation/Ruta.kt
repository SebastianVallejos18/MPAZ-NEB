package cl.duoc.mpazneb.ui.navigation

import cl.duoc.mpazneb.model.Perfil

sealed class Ruta(val ruta: String) {
    data object Login : Ruta("login")
    data object InicioAdmin : Ruta("inicio_admin")
    data object InicioAsistente : Ruta("inicio_asistente")
    data object InicioEstudiante : Ruta("inicio_estudiante")

    data object FormularioActividad : Ruta("formulario_actividad/{actividadId}") {
        fun conId(id: Long): String = "formulario_actividad/$id"
    }

    data object PreguntasActividad : Ruta("preguntas/{actividadId}") {
        fun conId(id: Long): String = "preguntas/$id"
    }

    data object FormularioPregunta : Ruta("formulario_pregunta/{actividadId}/{preguntaId}") {
        fun con(actividadId: Long, preguntaId: Long): String =
            "formulario_pregunta/$actividadId/$preguntaId"
    }

    data object IngresoEstudiante : Ruta("ingreso_estudiante")

    data object SesionEstudiante : Ruta("sesion_estudiante/{asignacionId}") {
        fun conId(id: Long): String = "sesion_estudiante/$id"
    }

    data object ResultadosAsistente : Ruta("resultados")

    data object DetalleAsignacion : Ruta("detalle_asignacion/{asignacionId}") {
        fun conId(id: Long): String = "detalle_asignacion/$id"
    }
}

// Decide a qué pantalla va cada perfil después de ingresar
fun rutaInicioDe(perfil: Perfil): String = when (perfil) {
    Perfil.Administrador -> Ruta.InicioAdmin.ruta
    Perfil.Asistente -> Ruta.InicioAsistente.ruta
    Perfil.Estudiante -> Ruta.InicioEstudiante.ruta
}