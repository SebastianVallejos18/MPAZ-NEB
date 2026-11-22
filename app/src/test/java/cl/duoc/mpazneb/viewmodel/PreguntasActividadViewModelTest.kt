package cl.duoc.mpazneb.viewmodel

import cl.duoc.mpazneb.MainDispatcherRule
import cl.duoc.mpazneb.actividadDePrueba
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.preguntaDePrueba
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class PreguntasActividadViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repositorio = mockk<ActividadRepository>()
    private val viewModel = PreguntasActividadViewModel(repositorio)

    @Test
    fun `cargar trae la actividad y sus preguntas`() = runTest {
        coEvery { repositorio.buscarPorId(10) } returns actividadDePrueba()
        coEvery { repositorio.preguntasDe(10) } returns listOf(preguntaDePrueba(), preguntaDePrueba(id = 101))

        viewModel.cargar(10)
        advanceUntilIdle()

        assertEquals("Sumas hasta 20", viewModel.uiState.value.nombreActividad)
        assertEquals(2, viewModel.preguntas.value.size)
    }

    @Test
    fun `no se puede eliminar la unica pregunta de una actividad disponible`() = runTest {
        coEvery { repositorio.buscarPorId(10) } returns actividadDePrueba(estado = EstadoActividad.Disponible)
        coEvery { repositorio.preguntasDe(10) } returns listOf(preguntaDePrueba())
        viewModel.cargar(10)
        advanceUntilIdle()

        viewModel.pedirEliminar(preguntaDePrueba())

        assertNull(viewModel.uiState.value.preguntaPorEliminar)
        assertNotNull(viewModel.uiState.value.mensaje)
    }

    @Test
    fun `eliminar una pregunta la borra y recarga la lista`() = runTest {
        coEvery { repositorio.buscarPorId(10) } returns actividadDePrueba(estado = EstadoActividad.Borrador)
        coEvery { repositorio.preguntasDe(10) } returnsMany listOf(listOf(preguntaDePrueba()), emptyList())
        coEvery { repositorio.eliminarPregunta(100) } returns Unit
        viewModel.cargar(10)
        advanceUntilIdle()

        viewModel.pedirEliminar(preguntaDePrueba())
        viewModel.confirmarEliminar()
        advanceUntilIdle()

        coVerify { repositorio.eliminarPregunta(100) }
        assertEquals(0, viewModel.preguntas.value.size)
        assertEquals("Pregunta eliminada", viewModel.uiState.value.mensaje)
    }

    @Test
    fun `cancelar eliminar limpia la seleccion`() {
        viewModel.pedirEliminar(preguntaDePrueba())
        viewModel.cancelarEliminar()
        assertNull(viewModel.uiState.value.preguntaPorEliminar)
    }

    @Test
    fun `errores de red se muestran como mensaje`() = runTest {
        coEvery { repositorio.buscarPorId(10) } throws IOException()

        viewModel.cargar(10)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.mensaje)
    }

    @Test
    fun `si falla el borrado se avisa`() = runTest {
        coEvery { repositorio.eliminarPregunta(any()) } throws IllegalStateException()

        viewModel.pedirEliminar(preguntaDePrueba())
        viewModel.confirmarEliminar()
        advanceUntilIdle()

        assertEquals("No se pudo eliminar la pregunta", viewModel.uiState.value.mensaje)
        assertNull(viewModel.uiState.value.preguntaPorEliminar)
    }
}
