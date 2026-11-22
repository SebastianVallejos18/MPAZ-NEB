package cl.duoc.mpazneb.data.remote.dto

// DTO = Data Transfer Object: la forma exacta del JSON que viaja por la red.
// Los enums viajan como texto ("LECTURA", "DISPONIBLE"...) y se convierten a sealed classes en Mapeadores.kt.
// Todos los campos tienen valor por defecto para que Gson no deje nulls si falta un campo.

// ---------- ms-usuarios ----------

data class UsuarioDto(
    val id: Long = 0,
    val alias: String = "",
    val perfil: String = "",
    val activo: Boolean = true,
    val ultimoAcceso: Long? = null
)

data class LoginRequest(
    val alias: String,
    val clave: String
)

// ---------- ms-actividades ----------

data class ActividadDto(
    val id: Long = 0,
    val nombre: String = "",
    val modulo: String = "",
    val cursoRecomendado: Int = 1,
    val contenido: String = "",
    val objetivoPedagogico: String = "",
    val dificultad: String = "",
    val instrucciones: String = "",
    val textoBase: String? = "",
    val intentosPermitidos: Int = 1,
    val lecturaEnVoz: Boolean = false,
    val estado: String = ""
)

data class PreguntaDto(
    val id: Long = 0,
    val actividadId: Long = 0,
    val tipo: String = "",
    val enunciado: String = "",
    val opcionA: String = "",
    val opcionB: String = "",
    val opcionC: String = "",
    val respuestaCorrecta: Int = 0,
    val procedimiento: String? = "",
    val retroalimentacion: String = ""
)

// ---------- ms-asignaciones ----------

data class AsignacionDto(
    val id: Long = 0,
    val actividadId: Long = 0,
    val estudianteId: Long = 0,
    val asistenteId: Long = 0,
    val codigoTemporal: String = "",
    val fecha: Long = 0,
    val estado: String = "",
    val intentos: Int = 0,
    val aciertos: Int = 0,
    val errores: Int = 0,
    val observacion: String? = ""
)

data class CrearAsignacionRequest(
    val actividadId: Long,
    val estudianteId: Long,
    val asistenteId: Long
)

data class ProgresoRequest(
    val estado: String,
    val intentos: Int,
    val aciertos: Int,
    val errores: Int
)

data class ObservacionRequest(
    val observacion: String
)

data class RespuestaDto(
    val id: Long = 0,
    val asignacionId: Long = 0,
    val preguntaId: Long = 0,
    val intento: Int = 1,
    val opcionElegida: Int = 0,
    val esCorrecta: Boolean = false,
    val fecha: Long = 0
)

data class RespuestaRequest(
    val preguntaId: Long,
    val intento: Int,
    val opcionElegida: Int,
    val esCorrecta: Boolean
)
