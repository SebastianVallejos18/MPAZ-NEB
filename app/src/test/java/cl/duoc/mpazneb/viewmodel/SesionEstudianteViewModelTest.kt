package cl.duoc.mpazneb.viewmodel

import android.app.Application
import cl.duoc.mpazneb.MainDispatcherRule
import cl.duoc.mpazneb.actividadDePrueba
import cl.duoc.mpazneb.asignacionDePrueba
import cl.duoc.mpazneb.data.repository.ActividadRepository
import cl.duoc.mpazneb.data.repository.AsignacionRepository
import cl.duoc.mpazneb.data.repository.PokemonRepository
import cl.duoc.mpazneb.model.Asignacion
import cl.duoc.mpazneb.model.EstadoAsignacion
import cl.duoc.mpazneb.model.Respuesta
import cl.duoc.mpazneb.pikachu
import cl.duoc.mpazneb.preguntaDePrueba
import cl.duoc.mpazneb.util.LectorVoz
import cl.duoc.mpazneb.util.Vibrador
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class SesionEstudianteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Las piezas de Android (contexto, voz, vibración) se reemplazan por mocks "relajados":
    // aceptan cualquier llamada sin hacer nada real
    private val aplicacion = mockk<Application>(relaxed = true)
    private val lector = mockk<LectorVoz>(relaxed = true)
    private val vibrador = mockk<Vibrador>(relaxed = true)

    private val actividadRepo = mockk<ActividadRepository>()
    private val asignacionRepo = mockk<AsignacionRepository>()
    private val pokemonRepo = mockk<PokemonRepository>()

    private lateinit var viewModel: SesionEstudianteViewModel

    @Before
    fun preparar() {
        every { lector.hablando } returns MutableStateFlow(false)
        every { lector.disponible } returns MutableStateFlow(true)
        // Por defecto el microservicio devuelve la asignación tal como se envió
        coEvery { asignacionRepo.actualizarProgreso(any()) } answers { firstArg() }
        coEvery { pokemonRepo.obtenerPremio() } returns pikachu
        viewModel = SesionEstudianteViewModel(
            aplicacion, actividadRepo, asignacionRepo, pokemonRepo, lector, vibrador
        )
    }

    private fun actividadCon(intentos: Int, cantidadPreguntas: Int = 1) {
        coEvery { actividadRepo.buscarPorId(10) } returns actividadDePrueba(intentos = intentos)
        coEvery { actividadRepo.preguntasDe(10) } returns
                (1..cantidadPreguntas).map { preguntaDePrueba(id = 100L + it, correcta = 1) }
    }

    // El microservicio suma el acierto o error y devuelve la asignación actualizada
    private fun registrarRespuestaSumando() {
        coEvery { asignacionRepo.registrarRespuesta(any()) } answers {
            val respuesta = firstArg<Respuesta>()
            val base = asignacionDePrueba(estado = EstadoAsignacion.EnDesarrollo, intentos = respuesta.intento)
            if (respuesta.esCorrecta) base.copy(aciertos = 1) else base.copy(errores = 1)
        }
    }

    private fun responder(opcion: Int) {
        viewModel.elegirOpcion(opcion)
        viewModel.pedirEnviar()
        viewModel.confirmarEnvio()
    }

    @Test
    fun `asignacion pendiente inicia el primer intento`() = runTest {
        coEvery { asignacionRepo.buscarPorId(50) } returns asignacionDePrueba()
        actividadCon(intentos = 2)

        viewModel.cargar(50)
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertFalse(estado.cargando)
        assertEquals(EtapaSesion.Instrucciones, estado.etapa)
        assertEquals(1, estado.intento)
        assertTrue(estado.vozActiva) // 1.° básico
        coVerify { asignacionRepo.actualizarProgreso(match { it.estado == EstadoAsignacion.EnDesarrollo && it.intentos == 1 }) }
        verify { lector.preparar() }
    }

    @Test
    fun `asignacion en desarrollo retoma la pregunta pendiente`() = runTest {
        coEvery { asignacionRepo.buscarPorId(50) } returns
                asignacionDePrueba(estado = EstadoAsignacion.EnDesarrollo, intentos = 1)
        coEvery { asignacionRepo.respuestasDe(50, 1) } returns
                listOf(Respuesta(asignacionId = 50, preguntaId = 101, intento = 1, opcionElegida = 1, esCorrecta = true))
        actividadCon(intentos = 2, cantidadPreguntas = 3)

        viewModel.cargar(50)
        advanceUntilIdle()

        assertEquals(EtapaSesion.Respondiendo, viewModel.uiState.value.etapa)
        assertEquals(1, viewModel.uiState.value.indice)
    }

    @Test
    fun `asignacion finalizada muestra el resultado`() = runTest {
        coEvery { asignacionRepo.buscarPorId(50) } returns
                asignacionDePrueba(estado = EstadoAsignacion.Finalizada, intentos = 2, aciertos = 1)
        actividadCon(intentos = 2)

        viewModel.cargar(50)
        advanceUntilIdle()

        assertEquals(EtapaSesion.Resultado, viewModel.uiState.value.etapa)
        assertEquals(pikachu, viewModel.uiState.value.premio)
    }

    @Test
    fun `sin asignacion o sin preguntas muestra mensaje`() = runTest {
        coEvery { asignacionRepo.buscarPorId(99) } returns null
        viewModel.cargar(99)
        advanceUntilIdle()
        assertEquals("No se pudo abrir la actividad", viewModel.uiState.value.mensaje)

        val otro = SesionEstudianteViewModel(aplicacion, actividadRepo, asignacionRepo, pokemonRepo, lector, vibrador)
        coEvery { asignacionRepo.buscarPorId(50) } returns asignacionDePrueba()
        actividadCon(intentos = 1, cantidadPreguntas = 0)
        otro.cargar(50)
        advanceUntilIdle()
        assertEquals("Esta actividad todavía no tiene preguntas", otro.uiState.value.mensaje)
    }

    @Test
    fun `sin conexion al cargar muestra el error`() = runTest {
        coEvery { asignacionRepo.buscarPorId(50) } throws IOException()

        viewModel.cargar(50)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.cargando)
        assertNotNull(viewModel.uiState.value.mensaje)
    }

    @Test
    fun `enviar sin elegir opcion pide elegir una`() {
        viewModel.pedirEnviar()
        assertEquals("Elige una respuesta antes de enviar", viewModel.uiState.value.mensaje)

        viewModel.elegirOpcion(0)
        viewModel.pedirEnviar()
        assertTrue(viewModel.uiState.value.confirmando)

        viewModel.cancelarEnvio()
        assertFalse(viewModel.uiState.value.confirmando)
    }

    @Test
    fun `respuesta correcta vibra suma acierto y al final entrega un pokemon`() = runTest {
        coEvery { asignacionRepo.buscarPorId(50) } returns asignacionDePrueba()
        actividadCon(intentos = 1)
        registrarRespuestaSumando()
        viewModel.cargar(50)
        advanceUntilIdle()
        viewModel.comenzar()

        responder(opcion = 1)
        advanceUntilIdle()

        assertEquals(EtapaSesion.Retroalimentacion, viewModel.uiState.value.etapa)
        assertTrue(viewModel.uiState.value.esCorrecta)
        assertEquals(1, viewModel.uiState.value.aciertos)
        verify { vibrador.acierto() }

        viewModel.siguiente()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertEquals(EtapaSesion.Resultado, estado.etapa)
        assertEquals(pikachu, estado.premio)
        assertFalse(estado.cargandoPremio)
        // Sin intentos restantes, la asignación se cierra en el microservicio
        coVerify { asignacionRepo.actualizarProgreso(match { it.estado == EstadoAsignacion.Finalizada }) }
    }

    @Test
    fun `sin aciertos no se pide premio a la PokeAPI`() = runTest {
        coEvery { asignacionRepo.buscarPorId(50) } returns asignacionDePrueba()
        actividadCon(intentos = 2)
        registrarRespuestaSumando()
        viewModel.cargar(50)
        advanceUntilIdle()

        responder(opcion = 0) // la correcta es la 1
        advanceUntilIdle()
        verify { vibrador.refuerzo() }

        viewModel.siguiente()
        advanceUntilIdle()

        assertEquals(EtapaSesion.Resultado, viewModel.uiState.value.etapa)
        assertNull(viewModel.uiState.value.premio)
        coVerify(exactly = 0) { pokemonRepo.obtenerPremio() }
    }

    @Test
    fun `si la PokeAPI falla la sesion termina igual`() = runTest {
        coEvery { pokemonRepo.obtenerPremio() } throws IOException()
        coEvery { asignacionRepo.buscarPorId(50) } returns asignacionDePrueba()
        actividadCon(intentos = 2)
        registrarRespuestaSumando()
        viewModel.cargar(50)
        advanceUntilIdle()

        responder(opcion = 1)
        advanceUntilIdle()
        viewModel.siguiente()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertEquals(EtapaSesion.Resultado, estado.etapa)
        assertNull(estado.premio)
        assertFalse(estado.cargandoPremio)
    }

    @Test
    fun `reintentar inicia un nuevo intento limpio`() = runTest {
        coEvery { asignacionRepo.buscarPorId(50) } returns asignacionDePrueba()
        actividadCon(intentos = 2)
        registrarRespuestaSumando()
        viewModel.cargar(50)
        advanceUntilIdle()
        responder(opcion = 1)
        advanceUntilIdle()
        viewModel.siguiente()
        advanceUntilIdle()

        viewModel.reintentar()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertEquals(2, estado.intento)
        assertEquals(0, estado.aciertos)
        assertEquals(0, estado.indice)
        assertNull(estado.premio)
        assertEquals(EtapaSesion.Respondiendo, estado.etapa)
        assertFalse(estado.puedeReintentar)
    }

    @Test
    fun `terminar cierra la asignacion y avisa a la pantalla`() = runTest {
        coEvery { asignacionRepo.buscarPorId(50) } returns asignacionDePrueba()
        actividadCon(intentos = 2)
        viewModel.cargar(50)
        advanceUntilIdle()

        var salio = false
        viewModel.terminar { salio = true }
        advanceUntilIdle()

        assertTrue(salio)
        coVerify { asignacionRepo.actualizarProgreso(match<Asignacion> { it.estado == EstadoAsignacion.Finalizada }) }
    }

    @Test
    fun `si falla guardar la respuesta se avisa y no avanza`() = runTest {
        coEvery { asignacionRepo.buscarPorId(50) } returns asignacionDePrueba()
        coEvery { asignacionRepo.registrarRespuesta(any()) } throws IOException()
        actividadCon(intentos = 1)
        viewModel.cargar(50)
        advanceUntilIdle()
        viewModel.comenzar()

        responder(opcion = 1)
        advanceUntilIdle()

        assertEquals(EtapaSesion.Respondiendo, viewModel.uiState.value.etapa)
        assertNotNull(viewModel.uiState.value.mensaje)
        assertFalse(viewModel.uiState.value.guardando)
    }

    @Test
    fun `leer en voz alta alterna entre leer y detener`() {
        viewModel.alternarLectura("Hola")
        verify { lector.leer("Hola") }
    }
}
