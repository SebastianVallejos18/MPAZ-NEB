package cl.duoc.mpazneb.util

import cl.duoc.mpazneb.errorHttp
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

class ErroresTest {

    @Test
    fun `sin conexion muestra el mensaje de microservicios apagados`() {
        assertEquals(MENSAJE_SIN_CONEXION, mensajeDeError(IOException("timeout"), "Por defecto"))
    }

    @Test
    fun `error http usa el mensaje que envia el microservicio`() {
        val error = errorHttp(409, "{\"mensaje\":\"Agrega al menos una pregunta\",\"estado\":409}")
        assertEquals("Agrega al menos una pregunta", mensajeDeError(error, "Por defecto"))
    }

    @Test
    fun `error http sin mensaje usa el texto por defecto`() {
        assertEquals("Por defecto", mensajeDeError(errorHttp(500, ""), "Por defecto"))
        assertEquals("Por defecto", mensajeDeError(errorHttp(500, "<html>no es json</html>"), "Por defecto"))
    }

    @Test
    fun `cualquier otro error usa el texto por defecto`() {
        assertEquals("Por defecto", mensajeDeError(IllegalStateException(), "Por defecto"))
    }
}
