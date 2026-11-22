package cl.duoc.mpazneb.util

import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException

const val MENSAJE_SIN_CONEXION =
    "No hay conexión con el servidor. Revisa que los microservicios estén encendidos"

// Traduce cualquier error de red a un mensaje para el usuario:
// - IOException: no se pudo conectar (microservicio apagado, sin internet, IP incorrecta)
// - HttpException: el microservicio respondió con error; se usa su campo "mensaje" si viene
fun mensajeDeError(error: Throwable, porDefecto: String): String = when (error) {
    is IOException -> MENSAJE_SIN_CONEXION
    is HttpException -> mensajeDelServidor(error) ?: porDefecto
    else -> porDefecto
}

// Lee {"mensaje": "..."} del cuerpo de la respuesta de error
private fun mensajeDelServidor(error: HttpException): String? =
    try {
        val cuerpo = error.response()?.errorBody()?.string()
        if (cuerpo.isNullOrBlank()) {
            null
        } else {
            JsonParser.parseString(cuerpo).asJsonObject.get("mensaje")?.asString
        }
    } catch (e: Exception) {
        null
    }
