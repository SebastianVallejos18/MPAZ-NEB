package cl.duoc.mpazneb.viewmodel

import cl.duoc.mpazneb.MainDispatcherRule
import cl.duoc.mpazneb.actividadDePrueba
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.errorHttp
import cl.duoc.mpazneb.model.Actividad
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.preguntaDePrueba
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FormularioActividadViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repositorio = mockk<ActividadRepository>()
    private val viewModel = FormularioActividadViewModel(repositorio)

    private fun llenarFormularioValido() {
        viewModel.onNombreChange("Sumas hasta 20")
        viewModel.onModuloChange(Modulo.Matematica)
        viewModel.onCursoChange(3)
        viewModel.onContenidoChange("Adición")
        viewModel.onObjetivoChange("Resolver sumas simples")
        viewModel.onDificultadChange(Dificultad.Media)
        viewModel.onInstruccionesChange("Resuelve cada suma con calma")
        viewModel.onTextoBaseChange("")
        viewModel.onIntentosChange("2")
        viewModel.onLecturaEnVozChange(true)
    }

    @Test
    fun `formulario vacio marca todos los errores`() = runTest {
        viewModel.cargar(0)
        viewModel.onGuardar()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertNotNull(estado.errorNombre)
        assertNotNull(estado.errorContenido)
        assertNotNull(estado.errorObjetivo)
        assertNotNull(estado.errorInstrucciones)
        assertNotNull(estado.errorTextoBase) // Lectura exige texto
        assertFalse(estado.guardado)
        coVerify(exactly = 0) { repositorio.crear(any()) }
    }

    @Test
    fun `no se puede crear disponible sin preguntas`() {
        llenarFormularioValido()
        viewModel.onEstadoChange(EstadoActividad.Disponible)
        viewModel.onGuardar()

        assertNotNull(viewModel.uiState.value.errorEstado)
    }

    @Test
    fun `los campos con error se revalidan al escribir`() {
        viewModel.onGuardar()
        llenarFormularioValido()
        viewModel.onEstadoChange(EstadoActividad.Borrador)

        val estado = viewModel.uiState.value
        assertNull(estado.errorNombre)
        assertNull(estado.errorContenido)
        assertNull(estado.errorObjetivo)
        assertNull(estado.errorInstrucciones)
        assertNull(estado.errorIntentos)
        assertNull(estado.errorEstado)
    }

    @Test
    fun `crear una actividad nueva guarda el id creado`() = runTest {
        val enviada = slot<Actividad>()
        coEvery { repositorio.crear(capture(enviada)) } returns actividadDePrueba(id = 11)
        viewModel.cargar(0)
        llenarFormularioValido()

        viewModel.onGuardar()
        advanceUntilIdle()

        assertEquals(11L, viewModel.uiState.value.idCreado)
        // En Matemática no se guarda texto base, y en 3.° básico se respeta la voz activada
        assertEquals("", enviada.captured.textoBase)
        assertTrue(enviada.captured.lecturaEnVoz)
        assertEquals(2, enviada.captured.intentosPermitidos)
    }

    @Test
    fun `editar carga la actividad y la actualiza`() = runTest {
        coEvery { repositorio.buscarPorId(10) } returns actividadDePrueba(curso = 2)
        coEvery { repositorio.preguntasDe(10) } returns listOf(preguntaDePrueba())
        coEvery { repositorio.actualizar(any()) } returns actividadDePrueba()

        viewModel.cargar(10)
        advanceUntilIdle()
        assertEquals("Sumas hasta 20", viewModel.uiState.value.nombre)
        assertEquals(1, viewModel.uiState.value.cantidadPreguntas)

        viewModel.onGuardar()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.guardado)
        coVerify { repositorio.actualizar(match { it.id == 10L }) }
    }

    @Test
    fun `el mensaje de regla del microservicio se muestra al usuario`() = runTest {
        coEvery { repositorio.crear(any()) } throws
                errorHttp(409, "{\"mensaje\":\"Agrega al menos una pregunta antes de dejarla disponible\"}")
        llenarFormularioValido()

        viewModel.onGuardar()
        advanceUntilIdle()

        assertEquals(
            "Agrega al menos una pregunta antes de dejarla disponible",
            viewModel.uiState.value.errorGeneral
        )
    }

    @Test
    fun `cargar dos veces no repite la consulta`() = runTest {
        coEvery { repositorio.buscarPorId(10) } returns null

        viewModel.cargar(10)
        viewModel.cargar(10)
        advanceUntilIdle()

        coVerify(exactly = 1) { repositorio.buscarPorId(10) }
    }
}
