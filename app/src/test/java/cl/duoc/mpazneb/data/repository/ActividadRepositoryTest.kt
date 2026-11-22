package cl.duoc.mpazneb.data.repository

import cl.duoc.mpazneb.actividadDePrueba
import cl.duoc.mpazneb.data.remote.aDto
import cl.duoc.mpazneb.data.remote.api.ActividadApi
import cl.duoc.mpazneb.errorHttp
import cl.duoc.mpazneb.model.EstadoActividad
import cl.duoc.mpazneb.model.Modulo
import cl.duoc.mpazneb.preguntaDePrueba
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ActividadRepositoryTest {

    private val api = mockk<ActividadApi>()
    private val repositorio = ActividadRepository(api)

    @Test
    fun `filtrar envia solo los filtros elegidos`() = runTest {
        coEvery { api.listar("MATEMATICA", null, null, "DISPONIBLE") } returns listOf(actividadDePrueba().aDto())

        val lista = repositorio.filtrar(Modulo.Matematica, null, null, EstadoActividad.Disponible)

        assertEquals(1, lista.size)
        assertEquals("Sumas hasta 20", lista.first().nombre)
    }

    @Test
    fun `buscar actividad inexistente devuelve null`() = runTest {
        coEvery { api.buscar(10) } returns actividadDePrueba().aDto()
        coEvery { api.buscar(99) } throws errorHttp(404)

        assertEquals(10L, repositorio.buscarPorId(10)?.id)
        assertNull(repositorio.buscarPorId(99))
    }

    @Test
    fun `crear y actualizar usan POST y PUT`() = runTest {
        val nueva = actividadDePrueba(id = 0)
        coEvery { api.crear(any()) } returns actividadDePrueba(id = 11).aDto()
        coEvery { api.actualizar(11, any()) } returns actividadDePrueba(id = 11).aDto()

        val creada = repositorio.crear(nueva)
        repositorio.actualizar(creada)

        assertEquals(11L, creada.id)
        coVerify { api.crear(nueva.aDto()) }
        coVerify { api.actualizar(11, any()) }
    }

    @Test
    fun `preguntas de una actividad`() = runTest {
        coEvery { api.preguntas(10) } returns listOf(preguntaDePrueba().aDto())
        coEvery { api.buscarPregunta(100) } returns preguntaDePrueba().aDto()
        coEvery { api.buscarPregunta(999) } throws errorHttp(404)

        assertEquals(1, repositorio.preguntasDe(10).size)
        assertEquals(100L, repositorio.buscarPregunta(100)?.id)
        assertNull(repositorio.buscarPregunta(999))
    }

    @Test
    fun `guardar pregunta nueva hace POST y una existente hace PUT`() = runTest {
        coEvery { api.crearPregunta(10, any()) } returns preguntaDePrueba(id = 101).aDto()
        coEvery { api.actualizarPregunta(100, any()) } returns preguntaDePrueba(id = 100).aDto()

        val creada = repositorio.guardarPregunta(preguntaDePrueba(id = 0))
        val editada = repositorio.guardarPregunta(preguntaDePrueba(id = 100))

        assertEquals(101L, creada.id)
        assertEquals(100L, editada.id)
        coVerify(exactly = 1) { api.crearPregunta(10, any()) }
        coVerify(exactly = 1) { api.actualizarPregunta(100, any()) }
    }

    @Test
    fun `eliminar pregunta llama al DELETE`() = runTest {
        coEvery { api.eliminarPregunta(100) } returns Unit
        repositorio.eliminarPregunta(100)
        coVerify { api.eliminarPregunta(100) }
    }
}
