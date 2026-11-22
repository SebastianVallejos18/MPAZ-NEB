package cl.duoc.mpazneb.viewmodel

import cl.duoc.mpazneb.MainDispatcherRule
import cl.duoc.mpazneb.actividadDePrueba
import cl.duoc.mpazneb.asignacionDePrueba
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.data.repository.AsignacionRepository
import cl.duoc.mpazneb.data.repository.UsuarioRepository
import cl.duoc.mpazneb.estudiante
import cl.duoc.mpazneb.preguntaDePrueba
import cl.duoc.mpazneb.util.MENSAJE_SIN_CONEXION
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ResultadosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val actividadRepo = mockk<ActividadRepository>()
    private val usuarioRepo = mockk<UsuarioRepository>()
    private val asignacionRepo = mockk<AsignacionRepository>()
    private val viewModel = ResultadosViewModel(actividadRepo, usuarioRepo, asignacionRepo)

    @Test
    fun `junta datos de los tres microservicios en un resumen`() = runTest {
        coEvery { asignacionRepo.deAsistente(2) } returns listOf(asignacionDePrueba(id = 50), asignacionDePrueba(id = 51))
        coEvery { actividadRepo.buscarPorId(10) } returns actividadDePrueba()
        coEvery { actividadRepo.preguntasDe(10) } returns listOf(preguntaDePrueba(), preguntaDePrueba(id = 101))
        coEvery { usuarioRepo.buscarPorId(3) } returns estudiante

        viewModel.cargar(2)
        advanceUntilIdle()

        val resumenes = viewModel.resumenes.value
        assertEquals(2, resumenes.size)
        assertEquals("estudiante1", resumenes.first().estudiante)
        assertEquals("Sumas hasta 20", resumenes.first().actividad)
        assertEquals(2, resumenes.first().totalPreguntas)
        // La misma actividad y el mismo estudiante se piden una sola vez
        coVerify(exactly = 1) { actividadRepo.buscarPorId(10) }
        coVerify(exactly = 1) { usuarioRepo.buscarPorId(3) }
    }

    @Test
    fun `si la actividad o el estudiante ya no existen se usan nombres genericos`() = runTest {
        coEvery { asignacionRepo.deAsistente(2) } returns listOf(asignacionDePrueba())
        coEvery { actividadRepo.buscarPorId(10) } returns null
        coEvery { usuarioRepo.buscarPorId(3) } returns null

        viewModel.cargar(2)
        advanceUntilIdle()

        val resumen = viewModel.resumenes.value.single()
        assertEquals("Actividad", resumen.actividad)
        assertEquals("Estudiante", resumen.estudiante)
        assertEquals(0, resumen.totalPreguntas)
    }

    @Test
    fun `sin conexion se muestra el error`() = runTest {
        coEvery { asignacionRepo.deAsistente(2) } throws IOException()

        viewModel.cargar(2)
        advanceUntilIdle()

        assertEquals(MENSAJE_SIN_CONEXION, viewModel.estado.value.error)
    }
}
