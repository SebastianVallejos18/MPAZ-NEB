package cl.duoc.mpazneb.viewmodel

import cl.duoc.mpazneb.MainDispatcherRule
import cl.duoc.mpazneb.actividadDePrueba
import cl.duoc.mpazneb.asignacionDePrueba
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.data.repository.AsignacionRepository
import cl.duoc.mpazneb.data.repository.UsuarioRepository
import cl.duoc.mpazneb.estudiante
import cl.duoc.mpazneb.model.Dificultad
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.model.Perfil
import cl.duoc.mpazneb.util.MENSAJE_SIN_CONEXION
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class AsistenteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val actividadRepo = mockk<ActividadRepository>()
    private val usuarioRepo = mockk<UsuarioRepository>()
    private val asignacionRepo = mockk<AsignacionRepository>()
    private val viewModel = AsistenteViewModel(actividadRepo, usuarioRepo, asignacionRepo)

    @Test
    fun `recargar trae solo actividades disponibles y estudiantes activos`() = runTest {
        coEvery { actividadRepo.filtrar(any(), any(), any(), EstadoActividad.Disponible) } returns
                listOf(actividadDePrueba())
        coEvery { usuarioRepo.listarPorPerfil(Perfil.Estudiante) } returns
                listOf(estudiante, estudiante.copy(id = 4, alias = "inactivo", activo = false))

        viewModel.recargar()
        advanceUntilIdle()

        assertEquals(1, viewModel.actividades.value.size)
        assertEquals(listOf(estudiante), viewModel.estudiantes.value)
    }

    @Test
    fun `los filtros recargan la lista`() = runTest {
        coEvery { actividadRepo.filtrar(any(), any(), any(), any()) } returns emptyList()
        coEvery { usuarioRepo.listarPorPerfil(any()) } returns emptyList()

        viewModel.filtrarPorModulo(Modulo.Matematica)
        viewModel.filtrarPorCurso(1)
        viewModel.filtrarPorDificultad(Dificultad.Facil)
        advanceUntilIdle()

        coVerify { actividadRepo.filtrar(Modulo.Matematica, 1, Dificultad.Facil, EstadoActividad.Disponible) }
    }

    @Test
    fun `sin conexion se muestra el error en la lista`() = runTest {
        coEvery { actividadRepo.filtrar(any(), any(), any(), any()) } throws IOException()

        viewModel.recargar()
        advanceUntilIdle()

        assertEquals(MENSAJE_SIN_CONEXION, viewModel.estadoLista.value.error)
    }

    @Test
    fun `confirmar sin elegir estudiante muestra error`() {
        viewModel.pedirAsignar(actividadDePrueba())
        viewModel.confirmarAsignacion(asistenteId = 2)

        assertEquals("Elige un estudiante para continuar", viewModel.uiState.value.errorAsignacion)
    }

    @Test
    fun `asignar crea la asignacion y muestra el codigo`() = runTest {
        coEvery { asignacionRepo.crear(10, 3, 2) } returns asignacionDePrueba()

        viewModel.pedirAsignar(actividadDePrueba())
        viewModel.elegirEstudiante(estudiante)
        viewModel.confirmarAsignacion(asistenteId = 2)
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertEquals("AB3K7P", estado.codigoGenerado)
        assertEquals("estudiante1", estado.aliasAsignado)
        assertNull(estado.actividadPorAsignar)

        viewModel.cerrarCodigo()
        assertNull(viewModel.uiState.value.codigoGenerado)
    }

    @Test
    fun `si falla la creacion se muestra el error`() = runTest {
        coEvery { asignacionRepo.crear(any(), any(), any()) } throws IllegalStateException()

        viewModel.pedirAsignar(actividadDePrueba())
        viewModel.elegirEstudiante(estudiante)
        viewModel.confirmarAsignacion(asistenteId = 2)
        advanceUntilIdle()

        assertEquals(
            "No se pudo crear la asignación. Intenta nuevamente",
            viewModel.uiState.value.errorAsignacion
        )
    }

    @Test
    fun `cancelar limpia la asignacion en curso`() {
        viewModel.pedirAsignar(actividadDePrueba())
        viewModel.elegirEstudiante(estudiante)
        viewModel.cancelarAsignacion()

        assertNull(viewModel.uiState.value.actividadPorAsignar)
        assertNull(viewModel.uiState.value.estudianteElegido)
    }
}
