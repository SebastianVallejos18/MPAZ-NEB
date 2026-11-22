package cl.duoc.mpazneb.viewmodel

import cl.duoc.mpazneb.MainDispatcherRule
import cl.duoc.mpazneb.actividadDePrueba
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.model.Pregunta
import cl.duoc.mpazneb.preguntaDePrueba
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class FormularioPreguntaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repositorio = mockk<ActividadRepository>()
    private val viewModel = FormularioPreguntaViewModel(repositorio)

    private fun llenarPreguntaValida() {
        viewModel.onTipoChange("SUMA")
        viewModel.onEnunciadoChange("¿Cuánto es 9 + 7?")
        viewModel.onOpcionAChange("16")
        viewModel.onOpcionBChange("15")
        viewModel.onOpcionCChange("17")
        viewModel.onCorrectaChange(0)
        viewModel.onProcedimientoChange("9 + 7 = 16")
        viewModel.onRetroalimentacionChange("Completa primero la decena")
    }

    @Test
    fun `pregunta nueva toma el modulo y el primer tipo de la actividad`() = runTest {
        coEvery { repositorio.buscarPorId(10) } returns actividadDePrueba()

        viewModel.cargar(10, 0)
        advanceUntilIdle()

        assertEquals(Modulo.Matematica, viewModel.uiState.value.modulo)
        assertEquals("SUMA", viewModel.uiState.value.tipo)
    }

    @Test
    fun `editar carga los datos de la pregunta`() = runTest {
        coEvery { repositorio.buscarPorId(10) } returns actividadDePrueba()
        coEvery { repositorio.buscarPregunta(100) } returns preguntaDePrueba()

        viewModel.cargar(10, 100)
        advanceUntilIdle()

        assertEquals(100L, viewModel.uiState.value.preguntaId)
        assertEquals("13", viewModel.uiState.value.opcionB)
        assertEquals(1, viewModel.uiState.value.correcta)
    }

    @Test
    fun `campos vacios marcan errores`() {
        viewModel.onGuardar()
        val estado = viewModel.uiState.value
        assertNotNull(estado.errorEnunciado)
        assertNotNull(estado.errorOpcionA)
        assertNotNull(estado.errorRetroalimentacion)
    }

    @Test
    fun `opciones repetidas marcan error`() {
        llenarPreguntaValida()
        viewModel.onOpcionBChange("16")
        viewModel.onGuardar()

        assertNotNull(viewModel.uiState.value.errorOpciones)
    }

    @Test
    fun `los errores se limpian al corregir`() {
        viewModel.onGuardar()
        llenarPreguntaValida()

        val estado = viewModel.uiState.value
        assertNull(estado.errorEnunciado)
        assertNull(estado.errorOpcionA)
        assertNull(estado.errorOpcionB)
        assertNull(estado.errorOpcionC)
        assertNull(estado.errorRetroalimentacion)
    }

    @Test
    fun `guardar envia la pregunta al repositorio`() = runTest {
        val enviada = slot<Pregunta>()
        coEvery { repositorio.buscarPorId(10) } returns actividadDePrueba()
        coEvery { repositorio.guardarPregunta(capture(enviada)) } returns preguntaDePrueba(id = 101)

        viewModel.cargar(10, 0)
        advanceUntilIdle()
        llenarPreguntaValida()
        viewModel.onGuardar()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.guardado)
        assertEquals(10L, enviada.captured.actividadId)
        assertEquals("9 + 7 = 16", enviada.captured.procedimiento)
    }

    @Test
    fun `si falla el guardado se muestra el error`() = runTest {
        coEvery { repositorio.buscarPorId(10) } returns actividadDePrueba()
        coEvery { repositorio.guardarPregunta(any()) } throws IOException()

        viewModel.cargar(10, 0)
        advanceUntilIdle()
        llenarPreguntaValida()
        viewModel.onGuardar()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorGeneral)
        coVerify(exactly = 1) { repositorio.guardarPregunta(any()) }
    }
}
