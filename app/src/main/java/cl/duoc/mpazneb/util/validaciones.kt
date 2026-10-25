package cl.duoc.mpazneb.util

import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo

// Cada función devuelve el mensaje de error, o null si el valor es válido
fun validarAlias(alias: String): String? = when {
    alias.isBlank() -> "Ingresa tu alias"
    alias.length < 3 -> "El alias debe tener al menos 3 caracteres"
    alias.length > 20 -> "El alias no puede superar los 20 caracteres"
    !alias.all { it.isLetterOrDigit() || it == '_' } -> "Usa solo letras, números o guion bajo"
    else -> null
}

fun validarClave(clave: String): String? = when {
    clave.isBlank() -> "Ingresa tu contraseña"
    clave.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
    else -> null
}

fun validarTextoObligatorio(texto: String, nombreCampo: String, minimo: Int, maximo: Int): String? = when {
    texto.isBlank() -> "Ingresa $nombreCampo"
    texto.trim().length < minimo -> "Debe tener al menos $minimo caracteres"
    texto.trim().length > maximo -> "No puede superar los $maximo caracteres"
    else -> null
}

fun validarNombreActividad(valor: String) = validarTextoObligatorio(valor, "el nombre de la actividad", 5, 60)

fun validarContenido(valor: String) = validarTextoObligatorio(valor, "el contenido", 3, 80)

fun validarObjetivo(valor: String) = validarTextoObligatorio(valor, "el objetivo pedagógico", 10, 200)

fun validarInstrucciones(valor: String) = validarTextoObligatorio(valor, "las instrucciones", 10, 200)

// El texto solo se exige en Lectura; en Matemática no aplica
fun validarTextoBase(modulo: Modulo, valor: String): String? =
    if (modulo == Modulo.Lectura) validarTextoObligatorio(valor, "el texto de lectura", 30, 600) else null

fun validarIntentos(texto: String): String? {
    val numero = texto.toIntOrNull()
    return when {
        texto.isBlank() -> "Ingresa los intentos permitidos"
        numero == null -> "Ingresa solo números"
        numero !in 1..5 -> "Debe ser un número entre 1 y 5"
        else -> null
    }
}

// Regla de negocio: una actividad sin preguntas no puede quedar disponible
fun validarEstado(estado: EstadoActividad, cantidadPreguntas: Int): String? =
    if (estado == EstadoActividad.Disponible && cantidadPreguntas == 0) {
        "Agrega al menos una pregunta antes de dejarla disponible"
    } else null

fun validarEnunciado(valor: String) = validarTextoObligatorio(valor, "el enunciado", 10, 200)

fun validarOpcion(valor: String) = validarTextoObligatorio(valor, "la opción", 1, 80)

fun validarRetroalimentacion(valor: String) = validarTextoObligatorio(valor, "la retroalimentación", 10, 200)

// El procedimiento solo se exige en Matemática
fun validarProcedimiento(modulo: Modulo, valor: String): String? =
    if (modulo == Modulo.Matematica) validarTextoObligatorio(valor, "el procedimiento", 3, 150) else null

fun validarOpcionesDistintas(a: String, b: String, c: String): String? {
    val normalizadas = listOf(a, b, c).map { it.trim().lowercase() }
    return if (normalizadas.toSet().size < 3) "Las tres opciones deben ser distintas" else null
}