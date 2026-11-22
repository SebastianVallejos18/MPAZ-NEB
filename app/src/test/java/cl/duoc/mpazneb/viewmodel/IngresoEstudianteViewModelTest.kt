package cl.duoc.mpazneb.viewmodel

import cl.duoc.mpazneb.MainDispatcherRule
import cl.duoc.mpazneb.asignacionDePrueba
import cl.duoc.mpazneb.data.repository.AsignacionRepository
import cl.duoc.mpazneb.data.repository.UsuarioRepository
import cl.duoc.mpazneb.estudiante
import cl.duoc.mpazneb.model.EstadoAsignacion
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

class IngresoEstudianteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val usuarioRepo = mockk<UsuarioRepository>()
    private val asignacionRepo = mockk<AsignacionRepository>()
    private val viewModel = IngresoEstudianteViewModel(usuarioRepo, asignacionRepo)

    @Test
    fun `el codigo se limpia y pasa a mayusculas`() {
        viewModel.onCodigoChange("ab-3k 7p99")
        assertEquals("AB3K7P", viewModel.uiState.value.codigo)
    }

    @Test
    fun `codigo incompleto muestra error sin llamar al microservicio`() = runTest {
        viewModel.onCodigoChange("AB3")
        viewModel.onIngresar()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorCodigo)
        coVerify(exactly = 0) { asignacionRepo.buscarPorCodigo(any()) }
    }

    @Test
    fun `codigo valido registra el acceso y abre la asignacion`() = runTest {
        coEvery { asignacionRepo.buscarPorCodigo("AB3K7P") } returns asignacionDePrueba()
        coEvery { usuarioRepo.buscarPorId(3) } returns estudiante
        coEvery { usuarioRepo.registrarAcceso(3) } returns estudiante.copy(ultimoAcceso = 99)

        viewModel.onCodigoChange("AB3K7P")
        viewModel.onIngresar()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertEquals(50L, estado.asignacionId)
        assertEquals(99L, estado.estudiante?.ultimoAcceso)
    }

    @Test
    fun `codigo inexistente muestra mensaje`() = runTest {
        coEvery { asignacionRepo.buscarPorCodigo(any()) } returns null

        viewModel.onCodigoChange("ZZZZZZ")
        viewModel.onIngresar()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorGeneral)
        assertNull(viewModel.uiState.value.asignacionId)
    }

    @Test
    fun `asignacion finalizada no deja entrar`() = runTest {
        coEvery { asignacionRepo.buscarPorCodigo(any()) } returns
                asignacionDePrueba(estado = EstadoAsignacion.Finalizada)
        coEvery { usuarioRepo.buscarPorId(3) } returns estudiante

        viewModel.onCodigoChange("AB3K7P")
        viewModel.onIngresar()
        advanceUntilIdle()

        assertEquals(
            "Esta actividad ya terminó. Pide un código nuevo a tu asistente",
            viewModel.uiState.value.errorGeneral
        )
    }

    @Test
    fun `estudiante inactivo no puede entrar`() = runTest {
        coEvery { asignacionRepo.buscarPorCodigo(any()) } returns asignacionDePrueba()
        coEvery { usuarioRepo.buscarPorId(3) } returns estudiante.copy(activo = false)

        viewModel.onCodigoChange("AB3K7P")
        viewModel.onIngresar()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.asignacionId)
        assertNotNull(viewModel.uiState.value.errorGeneral)
    }

    @Test
    fun `error del servidor muestra mensaje por defecto`() = runTest {
        coEvery { asignacionRepo.buscarPorCodigo(any()) } throws IllegalStateException()

        viewModel.onCodigoChange("AB3K7P")
        viewModel.onIngresar()
        advanceUntilIdle()

        assertEquals("Ocurrió un error. Intenta nuevamente", viewModel.uiState.value.errorGeneral)
    }
}
