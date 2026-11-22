package cl.duoc.mpazneb.model

import org.junit.Assert.assertEquals
import org.junit.Test

// Los textos que llegan desde los microservicios se convierten a las sealed classes de la app
class TiposTest {

    @Test
    fun `cada clave se convierte a su tipo y vuelve a la misma clave`() {
        listOf(Perfil.Administrador, Perfil.Asistente, Perfil.Estudiante)
            .forEach { assertEquals(it, Perfil.desde(it.clave)) }
        listOf(Modulo.Lectura, Modulo.Matematica)
            .forEach { assertEquals(it, Modulo.desde(it.clave)) }
        listOf(Dificultad.Facil, Dificultad.Media, Dificultad.Dificil)
            .forEach { assertEquals(it, Dificultad.desde(it.clave)) }
        listOf(EstadoActividad.Borrador, EstadoActividad.Disponible, EstadoActividad.Desactivada)
            .forEach { assertEquals(it, EstadoActividad.desde(it.clave)) }
        listOf(EstadoAsignacion.Pendiente, EstadoAsignacion.EnDesarrollo, EstadoAsignacion.Finalizada)
            .forEach { assertEquals(it, EstadoAsignacion.desde(it.clave)) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `una clave desconocida lanza error`() {
        Perfil.desde("PROFESOR")
    }
}
