package cl.duoc.mpazneb.viewmodel

import cl.duoc.mpazneb.MainDispatcherRule
import cl.duoc.mpazneb.actividadDePrueba
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.errorHttp
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class CatalogoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repositorio = mockk<ActividadRepository>()
    private val viewModel = CatalogoViewModel(repositorio)

    @Test
    fun `recargar trae las actividades del microservicio`() = runTest {
        coEvery { repositorio.filtrar(null, null, null, null) } returns listOf(actividadDePrueba())

        viewModel.recargar()
        advanceUntilIdle()

        assertEquals(1, viewModel.actividades.value.size)
        assertNull(viewModel.estado.value.error)
        assertFalse(viewModel.estado.value.cargando)
    }

    @Test
    fun `cada filtro se envia al repositorio`() = runTest {
        coEvery { repositorio.filtrar(any(), any(), any(), any()) } returns emptyList()

        viewModel.filtrarPorModulo(Modulo.Lectura)
        viewModel.filtrarPorCurso(2)
        viewModel.filtrarPorDificultad(Dificultad.Media)
        viewModel.filtrarPorEstado(EstadoActividad.Borrador)
        advanceUntilIdle()

        val filtros = viewModel.filtros.value
        assertEquals(Modulo.Lectura, filtros.modulo)
        assertEquals(2, filtros.curso)
        coVerify { repositorio.filtrar(Modulo.Lectura, 2, Dificultad.Media, EstadoActividad.Borrador) }
    }

    @Test
    fun `si el microservicio falla se muestra el error`() = runTest {
        coEvery { repositorio.filtrar(any(), any(), any(), any()) } throws errorHttp(500, "")

        viewModel.recargar()
        advanceUntilIdle()

        assertEquals("No se pudieron cargar las actividades", viewModel.estado.value.error)
    }
}
