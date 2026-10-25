package cl.duoc.mpazneb.model

sealed class Perfil(val clave: String) {
    data object Administrador : Perfil("ADMINISTRADOR")
    data object Asistente : Perfil("ASISTENTE")
    data object Estudiante : Perfil("ESTUDIANTE")

    companion object {
        fun desde(clave: String): Perfil = when (clave) {
            "ADMINISTRADOR" -> Administrador
            "ASISTENTE" -> Asistente
            "ESTUDIANTE" -> Estudiante
            else -> throw IllegalArgumentException("Perfil desconocido: $clave")
        }
    }
}

sealed class Modulo(val clave: String) {
    data object Lectura : Modulo("LECTURA")
    data object Matematica : Modulo("MATEMATICA")

    companion object {
        fun desde(clave: String): Modulo = when (clave) {
            "LECTURA" -> Lectura
            "MATEMATICA" -> Matematica
            else -> throw IllegalArgumentException("Modulo desconocido: $clave")
        }
    }
}

sealed class Dificultad(val clave: String) {
    data object Facil : Dificultad("FACIL")
    data object Media : Dificultad("MEDIA")
    data object Dificil : Dificultad("DIFICIL")

    companion object {
        fun desde(clave: String): Dificultad = when (clave) {
            "FACIL" -> Facil
            "MEDIA" -> Media
            "DIFICIL" -> Dificil
            else -> throw IllegalArgumentException("Dificultad desconocida: $clave")
        }
    }
}

sealed class EstadoActividad(val clave: String) {
    data object Borrador : EstadoActividad("BORRADOR")
    data object Disponible : EstadoActividad("DISPONIBLE")
    data object Desactivada : EstadoActividad("DESACTIVADA")

    companion object {
        fun desde(clave: String): EstadoActividad = when (clave) {
            "BORRADOR" -> Borrador
            "DISPONIBLE" -> Disponible
            "DESACTIVADA" -> Desactivada
            else -> throw IllegalArgumentException("Estado desconocido: $clave")
        }
    }
}

sealed class EstadoAsignacion(val clave: String) {
    data object Pendiente : EstadoAsignacion("PENDIENTE")
    data object EnDesarrollo : EstadoAsignacion("EN_DESARROLLO")
    data object Finalizada : EstadoAsignacion("FINALIZADA")

    companion object {
        fun desde(clave: String): EstadoAsignacion = when (clave) {
            "PENDIENTE" -> Pendiente
            "EN_DESARROLLO" -> EnDesarrollo
            "FINALIZADA" -> Finalizada
            else -> throw IllegalArgumentException("Estado desconocido: $clave")
        }
    }
}