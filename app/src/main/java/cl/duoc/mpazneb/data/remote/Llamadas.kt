package cl.duoc.mpazneb.data.remote

import retrofit2.HttpException

// Cuando el microservicio responde 404 (no existe), se devuelve null en vez de lanzar un error.
// Cualquier otro error (sin conexión, 500, etc.) se deja pasar para que el ViewModel lo muestre.
suspend fun <T> nuloSiNoExiste(llamada: suspend () -> T): T? =
    try {
        llamada()
    } catch (e: HttpException) {
        if (e.code() == 404) null else throw e
    }
