package cl.duoc.mpazneb.data.repository

import cl.duoc.mpazneb.data.remote.api.UsuarioApi
import cl.duoc.mpazneb.data.remote.dto.LoginRequest
import cl.duoc.mpazneb.data.remote.dto.UsuarioDto
import cl.duoc.mpazneb.errorHttp
import cl.duoc.mpazneb.model.Perfil
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.HttpException

class UsuarioRepositoryTest {

    // Mock de la interfaz de Retrofit: no se conecta a ningún microservicio
    private val api = mockk<UsuarioApi>()
    private val repositorio = UsuarioRepository(api)

    private val dtoAsistente = UsuarioDto(id = 2, alias = "asistente1", perfil = "ASISTENTE")

    @Test
    fun `login correcto devuelve el usuario`() = runTest {
        coEvery { api.login(LoginRequest("asistente1", "asist123")) } returns dtoAsistente

        val usuario = repositorio.login("asistente1", "asist123")

        assertEquals("asistente1", usuario?.alias)
        assertEquals(Perfil.Asistente, usuario?.perfil)
    }

    @Test
    fun `login con 401 devuelve null`() = runTest {
        coEvery { api.login(any()) } throws errorHttp(401, "")
        assertNull(repositorio.login("asistente1", "malaclave"))
    }

    @Test(expected = HttpException::class)
    fun `login con error del servidor se relanza`() = runTest {
        coEvery { api.login(any()) } throws errorHttp(500)
        repositorio.login("asistente1", "asist123")
    }

    @Test
    fun `buscar por id devuelve null si no existe`() = runTest {
        coEvery { api.buscar(2) } returns dtoAsistente
        coEvery { api.buscar(99) } throws errorHttp(404)

        assertEquals("asistente1", repositorio.buscarPorId(2)?.alias)
        assertNull(repositorio.buscarPorId(99))
    }

    @Test
    fun `listar por perfil envia la clave del perfil`() = runTest {
        coEvery { api.listar("ESTUDIANTE") } returns listOf(
            UsuarioDto(id = 3, alias = "estudiante1", perfil = "ESTUDIANTE"),
            UsuarioDto(id = 4, alias = "estudiante2", perfil = "ESTUDIANTE")
        )

        val estudiantes = repositorio.listarPorPerfil(Perfil.Estudiante)

        assertEquals(2, estudiantes.size)
        coVerify { api.listar("ESTUDIANTE") }
    }

    @Test
    fun `registrar acceso llama al endpoint de acceso`() = runTest {
        coEvery { api.registrarAcceso(3) } returns UsuarioDto(id = 3, alias = "estudiante1", perfil = "ESTUDIANTE", ultimoAcceso = 123)
        assertEquals(123L, repositorio.registrarAcceso(3).ultimoAcceso)
    }
}
