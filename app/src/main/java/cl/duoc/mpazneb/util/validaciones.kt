package cl.duoc.mpazneb.util

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