package cl.duoc.mpazneb.viewmodel

import cl.duoc.mpazneb.MainDispatcherRule
import cl.duoc.mpazneb.actividadDePrueba
import cl.duoc.mpazneb.asignacionDePrueba
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.data.repository.AsignacionRepository
import cl.duoc.mpazneb.data.repository.UsuarioRepository
import cl.duoc.mpazneb.estudiante
import cl.duoc.mpazneb.model.EstadoAsignacion
import cl.duoc.mpazneb.model.Respuesta
import cl.duoc.mpazneb.preguntaDePrueba
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetalleAsignacionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val actividadRepo = mockk<ActividadRepository>()
    private val usuarioRepo = mockk<UsuarioRepository>()
    private val asignacionRepo = mockk<AsignacionRepository>()
    private val viewModel = DetalleAsignacionViewModel(actividadRepo, usuarioRepo, asignacionRepo)

    private fun prepararDetalle() {
        coEvery { asignacionRepo.buscarPorId(50) } returns asignacionDePrueba(estado = EstadoAsignacion.EnDesarrollo)
        coEvery { usuarioRepo.buscarPorId(3) } returns estudiante
        coEvery { actividadRepo.buscarPorId(10) } returns actividadDePrueba()
        coEvery { actividadRepo.preguntasDe(10) } returns listOf(preguntaDePrueba(id = 100), preguntaDePrueba(id = 101))
        coEvery { asignacionRepo.respuestasDe(50) } returns listOf(
            Respuesta(id = 1, asignacionId = 50, preguntaId = 100, intento = 1, opcionElegida = 1, esCorrecta = true),
            Respuesta(id = 2, asignacionId = 50, preguntaId = 101, intento = 1, opcionElegida = 0, esCorrecta = false),
            // Respuesta de una pregunta que ya no existe: se ignora
            Respuesta(id = 3, asignacionId = 50, preguntaId = 999, intento = 1, opcionElegida = 0, esCorrecta = false)
        )
    }

    @Test
    fun `cargar arma el detalle con las respuestas de cada pregunta`() = runTest {
        prepararDetalle()

        viewModel.cargar(50)
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertFalse(estado.cargando)
        assertEquals("estudiante1", estado.estudiante)
        assertEquals(2, estado.totalPreguntas)
        assertEquals(2, estado.respuestas.size)
        assertEquals("B: 13", estado.respuestas.first().elegida)
        assertEquals(2, estado.respuestas[1].numeroPregunta)
    }

    @Test
    fun `asignacion inexistente muestra mensaje`() = runTest {
        coEvery { asignacionRepo.buscarPorId(99) } returns null

        viewModel.cargar(99)
        advanceUntilIdle()

        assertEquals("No se pudo abrir el detalle", viewModel.uiState.value.mensaje)
    }

    @Test
    fun `error al cargar se muestra`() = runTest {
        coEvery { asignacionRepo.buscarPorId(50) } throws IllegalStateException()

        viewModel.cargar(50)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.cargando)
        assertNotNull(viewModel.uiState.value.mensaje)
    }

    @Test
    fun `guardar observacion valida el largo y la envia`() = runTest {
        prepararDetalle()
        coEvery { asignacionRepo.guardarObservacion(50, "Practicar restas") } returns
                asignacionDePrueba().copy(observacion = "Practicar restas")
        viewModel.cargar(50)
        advanceUntilIdle()

        viewModel.onObservacionChange("a".repeat(301))
        viewModel.onGuardarObservacion()
        assertNotNull(viewModel.uiState.value.errorObservacion)

        viewModel.onObservacionChange("  Practicar restas  ")
        viewModel.onGuardarObservacion()
        advanceUntilIdle()

        assertEquals("Observación guardada", viewModel.uiState.value.mensaje)
        coVerify { asignacionRepo.guardarObservacion(50, "Practicar restas") }
    }

    @Test
    fun `si falla guardar la observacion se avisa`() = runTest {
        prepararDetalle()
        coEvery { asignacionRepo.guardarObservacion(any(), any()) } throws IllegalStateException()
        viewModel.cargar(50)
        advanceUntilIdle()

        viewModel.onObservacionChange("Nota")
        viewModel.onGuardarObservacion()
        advanceUntilIdle()

        assertEquals("No se pudo guardar la observación", viewModel.uiState.value.mensaje)
    }

    @Test
    fun `eliminar pide confirmacion y luego borra`() = runTest {
        prepararDetalle()
        coEvery { asignacionRepo.eliminar(50) } returns Unit
        viewModel.cargar(50)
        advanceUntilIdle()

        viewModel.pedirEliminar()
        assertTrue(viewModel.uiState.value.confirmandoEliminar)
        viewModel.cancelarEliminar()
        assertFalse(viewModel.uiState.value.confirmandoEliminar)

        viewModel.pedirEliminar()
        viewModel.confirmarEliminar()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.eliminada)
        coVerify { asignacionRepo.eliminar(50) }
    }

    @Test
    fun `si falla eliminar no se marca como eliminada`() = runTest {
        prepararDetalle()
        coEvery { asignacionRepo.eliminar(any()) } throws IllegalStateException()
        viewModel.cargar(50)
        advanceUntilIdle()

        viewModel.confirmarEliminar()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.eliminada)
        assertEquals("No se pudo eliminar la asignación", viewModel.uiState.value.mensaje)
    }
}
