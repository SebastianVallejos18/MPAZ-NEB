package cl.duoc.mpazneb.data.repository

import cl.duoc.mpazneb.asignacionDePrueba
import cl.duoc.mpazneb.data.remote.api.AsignacionApi
import cl.duoc.mpazneb.data.remote.dto.AsignacionDto
import cl.duoc.mpazneb.data.remote.dto.CrearAsignacionRequest
import cl.duoc.mpazneb.data.remote.dto.ObservacionRequest
import cl.duoc.mpazneb.data.remote.dto.ProgresoRequest
import cl.duoc.mpazneb.data.remote.dto.RespuestaDto
import cl.duoc.mpazneb.data.remote.dto.RespuestaRequest
import cl.duoc.mpazneb.errorHttp
import cl.duoc.mpazneb.model.EstadoAsignacion
import cl.duoc.mpazneb.model.Respuesta
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AsignacionRepositoryTest {

    private val api = mockk<AsignacionApi>()
    private val repositorio = AsignacionRepository(api)

    private val dto = AsignacionDto(
        id = 50, actividadId = 10, estudianteId = 3, asistenteId = 2,
        codigoTemporal = "AB3K7P", estado = "PENDIENTE"
    )

    @Test
    fun `crear envia actividad estudiante y asistente y recibe el codigo`() = runTest {
        coEvery { api.crear(CrearAsignacionRequest(10, 3, 2)) } returns dto

        val asignacion = repositorio.crear(10, 3, 2)

        assertEquals("AB3K7P", asignacion.codigoTemporal)
        assertEquals(EstadoAsignacion.Pendiente, asignacion.estado)
    }

    @Test
    fun `buscar por id o codigo devuelve null si no existe`() = runTest {
        coEvery { api.buscar(50) } returns dto
        coEvery { api.buscar(99) } throws errorHttp(404)
        coEvery { api.buscarPorCodigo("AB3K7P") } returns dto
        coEvery { api.buscarPorCodigo("ZZZZZZ") } throws errorHttp(404)

        assertEquals(50L, repositorio.buscarPorId(50)?.id)
        assertNull(repositorio.buscarPorId(99))
        assertEquals(50L, repositorio.buscarPorCodigo("AB3K7P")?.id)
        assertNull(repositorio.buscarPorCodigo("ZZZZZZ"))
    }

    @Test
    fun `asignaciones de una asistente`() = runTest {
        coEvery { api.deAsistente(2) } returns listOf(dto, dto.copy(id = 51))
        assertEquals(2, repositorio.deAsistente(2).size)
    }

    @Test
    fun `actualizar progreso envia estado intentos aciertos y errores`() = runTest {
        val esperado = ProgresoRequest("EN_DESARROLLO", 1, 0, 0)
        coEvery { api.actualizarProgreso(50, esperado) } returns dto.copy(estado = "EN_DESARROLLO", intentos = 1)

        val actualizada = repositorio.actualizarProgreso(
            asignacionDePrueba(estado = EstadoAsignacion.EnDesarrollo, intentos = 1)
        )

        assertEquals(EstadoAsignacion.EnDesarrollo, actualizada.estado)
        coVerify { api.actualizarProgreso(50, esperado) }
    }

    @Test
    fun `guardar observacion y eliminar`() = runTest {
        coEvery { api.guardarObservacion(50, ObservacionRequest("Practicar restas")) } returns
                dto.copy(observacion = "Practicar restas")
        coEvery { api.eliminar(50) } returns Unit

        assertEquals("Practicar restas", repositorio.guardarObservacion(50, "Practicar restas").observacion)
        repositorio.eliminar(50)
        coVerify { api.eliminar(50) }
    }

    @Test
    fun `respuestas de un intento y registro de una respuesta`() = runTest {
        coEvery { api.respuestas(50, 1) } returns listOf(RespuestaDto(id = 1, asignacionId = 50, preguntaId = 100))
        coEvery { api.respuestas(50, null) } returns emptyList()
        coEvery { api.registrarRespuesta(50, RespuestaRequest(100, 1, 1, true)) } returns dto.copy(aciertos = 1)

        assertEquals(1, repositorio.respuestasDe(50, 1).size)
        assertEquals(0, repositorio.respuestasDe(50).size)

        val actualizada = repositorio.registrarRespuesta(
            Respuesta(asignacionId = 50, preguntaId = 100, intento = 1, opcionElegida = 1, esCorrecta = true)
        )
        assertEquals(1, actualizada.aciertos)
    }
}
