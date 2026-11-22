package cl.duoc.mpazneb.util

import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.EstadoAsignacion
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.preguntaDePrueba
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReglasYEtiquetasTest {

    @Test
    fun `lectura en voz depende del curso`() {
        assertTrue(permiteLecturaEnVoz(1, false))
        assertTrue(permiteLecturaEnVoz(2, false))
        assertTrue(permiteLecturaEnVoz(3, true))
        assertFalse(permiteLecturaEnVoz(3, false))
        assertFalse(permiteLecturaEnVoz(4, true))
    }

    @Test
    fun `premio pokemon solo con al menos un acierto`() {
        assertFalse(mereceRecompensa(0))
        assertTrue(mereceRecompensa(1))
        assertTrue(mereceRecompensa(3))
    }

    @Test
    fun `etiquetas de los tipos del caso`() {
        assertEquals("Lectura", etiquetaModulo(Modulo.Lectura))
        assertEquals("Matemática", etiquetaModulo(Modulo.Matematica))
        assertEquals("Fácil", etiquetaDificultad(Dificultad.Facil))
        assertEquals("Media", etiquetaDificultad(Dificultad.Media))
        assertEquals("Difícil", etiquetaDificultad(Dificultad.Dificil))
        assertEquals("Borrador", etiquetaEstado(EstadoActividad.Borrador))
        assertEquals("Disponible", etiquetaEstado(EstadoActividad.Disponible))
        assertEquals("Desactivada", etiquetaEstado(EstadoActividad.Desactivada))
        assertEquals("Pendiente", etiquetaEstadoAsignacion(EstadoAsignacion.Pendiente))
        assertEquals("En desarrollo", etiquetaEstadoAsignacion(EstadoAsignacion.EnDesarrollo))
        assertEquals("Finalizada", etiquetaEstadoAsignacion(EstadoAsignacion.Finalizada))
        assertEquals("3° básico", etiquetaCurso(3))
    }

    @Test
    fun `tipos de pregunta segun el modulo`() {
        assertEquals(listOf("EXPLICITA", "IMPLICITA", "VOCABULARIO"), tiposDePregunta(Modulo.Lectura))
        assertEquals(4, tiposDePregunta(Modulo.Matematica).size)
        listOf("EXPLICITA", "IMPLICITA", "VOCABULARIO", "SUMA", "RESTA", "MULTIPLICACION", "PROBLEMA")
            .forEach { assertTrue(etiquetaTipoPregunta(it) != it) }
        assertEquals("OTRO", etiquetaTipoPregunta("OTRO"))
    }

    @Test
    fun `letras de las opciones`() {
        assertEquals("A", letraOpcion(0))
        assertEquals("B", letraOpcion(1))
        assertEquals("C", letraOpcion(2))
    }

    @Test
    fun `tipos de pokemon se traducen al espanol`() {
        assertEquals("Eléctrico", etiquetaTipoPokemon("electric"))
        assertEquals("Fuego", etiquetaTipoPokemon("fire"))
        assertEquals("Planta", etiquetaTipoPokemon("grass"))
        val todos = listOf(
            "normal", "fire", "water", "grass", "electric", "ice", "fighting", "poison", "ground",
            "flying", "psychic", "bug", "rock", "ghost", "dragon", "dark", "steel", "fairy"
        )
        todos.forEach { assertTrue(etiquetaTipoPokemon(it).isNotBlank()) }
        // Un tipo desconocido se muestra con mayúscula inicial
        assertEquals("Stellar", etiquetaTipoPokemon("stellar"))
    }

    @Test
    fun `opciones y texto para leer de una pregunta`() {
        val pregunta = preguntaDePrueba()
        assertEquals(listOf("12", "13", "14"), pregunta.opciones())
        val texto = pregunta.textoParaLeer()
        assertTrue(texto.startsWith("¿Cuánto es 8 + 5?"))
        assertTrue(texto.contains("Opción B: 13"))
    }

    @Test
    fun `formato de fecha`() {
        assertEquals(16, formatearFecha(1_700_000_000_000).length)
    }
}
