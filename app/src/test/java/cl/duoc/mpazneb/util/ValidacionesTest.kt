package cl.duoc.mpazneb.util

import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesTest {

    @Test
    fun `alias valido no tiene error`() {
        assertNull(validarAlias("asistente_1"))
    }

    @Test
    fun `alias vacio corto largo o con simbolos tiene error`() {
        assertEquals("Ingresa tu alias", validarAlias(""))
        assertNotNull(validarAlias("ab"))
        assertNotNull(validarAlias("a".repeat(21)))
        assertEquals("Usa solo letras, números o guion bajo", validarAlias("hola mundo!"))
    }

    @Test
    fun `clave debe tener al menos 6 caracteres`() {
        assertEquals("Ingresa tu contraseña", validarClave(""))
        assertNotNull(validarClave("123"))
        assertNull(validarClave("admin123"))
    }

    @Test
    fun `texto obligatorio respeta minimo y maximo`() {
        assertEquals("Ingresa el nombre", validarTextoObligatorio("  ", "el nombre", 3, 10))
        assertEquals("Debe tener al menos 3 caracteres", validarTextoObligatorio("ab", "el nombre", 3, 10))
        assertEquals("No puede superar los 10 caracteres", validarTextoObligatorio("a".repeat(11), "el nombre", 3, 10))
        assertNull(validarTextoObligatorio("válido", "el nombre", 3, 10))
    }

    @Test
    fun `campos de actividad usan sus limites`() {
        assertNotNull(validarNombreActividad("Uno"))
        assertNull(validarNombreActividad("Sumas hasta 20"))
        assertNull(validarContenido("Adición"))
        assertNotNull(validarObjetivo("Corto"))
        assertNull(validarObjetivo("Resolver sumas simples"))
        assertNotNull(validarInstrucciones(""))
        assertNull(validarInstrucciones("Lee con calma y responde"))
    }

    @Test
    fun `texto base solo se exige en lectura`() {
        assertNotNull(validarTextoBase(Modulo.Lectura, "Muy corto"))
        assertNull(validarTextoBase(Modulo.Lectura, "Misifú es un gato gris que mira la lluvia."))
        assertNull(validarTextoBase(Modulo.Matematica, ""))
    }

    @Test
    fun `intentos deben ser un numero entre 1 y 5`() {
        assertEquals("Ingresa los intentos permitidos", validarIntentos(""))
        assertEquals("Ingresa solo números", validarIntentos("dos"))
        assertNotNull(validarIntentos("0"))
        assertNotNull(validarIntentos("6"))
        assertNull(validarIntentos("3"))
    }

    @Test
    fun `actividad disponible necesita al menos una pregunta`() {
        assertNotNull(validarEstado(EstadoActividad.Disponible, 0))
        assertNull(validarEstado(EstadoActividad.Disponible, 1))
        assertNull(validarEstado(EstadoActividad.Borrador, 0))
    }

    @Test
    fun `campos de pregunta usan sus limites`() {
        assertNotNull(validarEnunciado("¿2+2?"))
        assertNull(validarEnunciado("¿Cuánto es 8 + 5?"))
        assertNotNull(validarOpcion(""))
        assertNull(validarOpcion("13"))
        assertNotNull(validarRetroalimentacion("Bien"))
        assertNull(validarRetroalimentacion("Cuenta desde el 8"))
    }

    @Test
    fun `procedimiento solo se exige en matematica`() {
        assertNotNull(validarProcedimiento(Modulo.Matematica, ""))
        assertNull(validarProcedimiento(Modulo.Matematica, "8 + 5 = 13"))
        assertNull(validarProcedimiento(Modulo.Lectura, ""))
    }

    @Test
    fun `opciones repetidas sin importar mayusculas ni espacios tienen error`() {
        assertNotNull(validarOpcionesDistintas("Gris", " gris ", "Negro"))
        assertNull(validarOpcionesDistintas("Gris", "Negro", "Blanco"))
    }

    @Test
    fun `codigo debe tener 6 caracteres`() {
        assertNotNull(validarCodigo(""))
        assertNotNull(validarCodigo("ABC"))
        assertNull(validarCodigo("AB3K7P"))
    }

    @Test
    fun `observacion no puede superar 300 caracteres`() {
        assertNull(validarObservacion("Practicar restas"))
        assertNotNull(validarObservacion("a".repeat(301)))
    }
}
