package cl.duoc.mpazneb.data.remote

import cl.duoc.mpazneb.actividadDePrueba
import cl.duoc.mpazneb.asignacionDePrueba
import cl.duoc.mpazneb.data.remote.dto.ActividadDto
import cl.duoc.mpazneb.data.remote.dto.ArteOficialDto
import cl.duoc.mpazneb.data.remote.dto.AsignacionDto
import cl.duoc.mpazneb.data.remote.dto.OtrosSpritesDto
import cl.duoc.mpazneb.data.remote.dto.PokemonDto
import cl.duoc.mpazneb.data.remote.dto.PreguntaDto
import cl.duoc.mpazneb.data.remote.dto.RespuestaDto
import cl.duoc.mpazneb.data.remote.dto.SpritesDto
import cl.duoc.mpazneb.data.remote.dto.TipoDto
import cl.duoc.mpazneb.data.remote.dto.TipoSlotDto
import cl.duoc.mpazneb.data.remote.dto.UsuarioDto
import cl.duoc.mpazneb.model.EstadoAsignacion
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.model.Perfil
import cl.duoc.mpazneb.model.Respuesta
import cl.duoc.mpazneb.preguntaDePrueba
import org.junit.Assert.assertEquals
import org.junit.Test

class MapeadoresTest {

    @Test
    fun `usuario del microservicio se convierte al modelo`() {
        val usuario = UsuarioDto(id = 2, alias = "asistente1", perfil = "ASISTENTE", activo = true, ultimoAcceso = null)
            .aModelo()
        assertEquals(Perfil.Asistente, usuario.perfil)
        assertEquals(0L, usuario.ultimoAcceso) // null se convierte en 0
    }

    @Test
    fun `actividad ida y vuelta conserva los datos`() {
        val original = actividadDePrueba()
        val dto = original.aDto()
        assertEquals("MATEMATICA", dto.modulo)
        assertEquals("DISPONIBLE", dto.estado)
        assertEquals(original, dto.aModelo())
    }

    @Test
    fun `texto base nulo se convierte en texto vacio`() {
        val dto = ActividadDto(modulo = "LECTURA", dificultad = "MEDIA", estado = "BORRADOR", textoBase = null)
        val actividad = dto.aModelo()
        assertEquals(Modulo.Lectura, actividad.modulo)
        assertEquals("", actividad.textoBase)
    }

    @Test
    fun `pregunta ida y vuelta conserva los datos`() {
        val original = preguntaDePrueba()
        assertEquals(original, original.aDto().aModelo())
        assertEquals("", PreguntaDto(procedimiento = null).aModelo().procedimiento)
    }

    @Test
    fun `asignacion se convierte y genera el cuerpo de progreso`() {
        val asignacion = AsignacionDto(id = 1, estado = "EN_DESARROLLO", intentos = 1, aciertos = 2, observacion = null)
            .aModelo()
        assertEquals(EstadoAsignacion.EnDesarrollo, asignacion.estado)
        assertEquals("", asignacion.observacion)

        val progreso = asignacionDePrueba(estado = EstadoAsignacion.Finalizada, intentos = 2, aciertos = 3, errores = 1)
            .aProgreso()
        assertEquals("FINALIZADA", progreso.estado)
        assertEquals(2, progreso.intentos)
        assertEquals(3, progreso.aciertos)
        assertEquals(1, progreso.errores)
    }

    @Test
    fun `respuesta se convierte en ambos sentidos`() {
        val respuesta = RespuestaDto(id = 7, asignacionId = 50, preguntaId = 100, intento = 1, opcionElegida = 2, esCorrecta = true)
            .aModelo()
        assertEquals(50L, respuesta.asignacionId)

        val cuerpo = Respuesta(asignacionId = 50, preguntaId = 100, intento = 2, opcionElegida = 0, esCorrecta = false)
            .aRequest()
        assertEquals(100L, cuerpo.preguntaId)
        assertEquals(2, cuerpo.intento)
        assertEquals(false, cuerpo.esCorrecta)
    }

    @Test
    fun `pokemon usa la ilustracion oficial y ordena los tipos`() {
        val dto = PokemonDto(
            id = 1,
            name = "bulbasaur",
            sprites = SpritesDto(
                frontDefault = "https://sprite/1.png",
                other = OtrosSpritesDto(officialArtwork = ArteOficialDto(frontDefault = "https://arte/1.png"))
            ),
            types = listOf(TipoSlotDto(2, TipoDto("poison")), TipoSlotDto(1, TipoDto("grass")))
        )
        val pokemon = dto.aModelo()
        assertEquals("Bulbasaur", pokemon.nombre)
        assertEquals("https://arte/1.png", pokemon.imagenUrl)
        assertEquals(listOf("grass", "poison"), pokemon.tipos)
    }

    @Test
    fun `pokemon sin ilustracion oficial usa el sprite normal`() {
        val dto = PokemonDto(id = 25, name = "pikachu", sprites = SpritesDto(frontDefault = "https://sprite/25.png"))
        assertEquals("https://sprite/25.png", dto.aModelo().imagenUrl)
        assertEquals("", PokemonDto().aModelo().imagenUrl)
    }
}
