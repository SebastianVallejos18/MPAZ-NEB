package cl.duoc.mpazneb.viewmodel

import cl.duoc.mpazneb.MainDispatcherRule
import cl.duoc.mpazneb.admin
import cl.duoc.mpazneb.data.repository.UsuarioRepository
import cl.duoc.mpazneb.estudiante
import cl.duoc.mpazneb.util.MENSAJE_SIN_CONEXION
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repositorio = mockk<UsuarioRepository>()
    private val viewModel = LoginViewModel(repositorio)

    private fun escribir(alias: String, clave: String) {
        viewModel.onAliasChange(alias)
        viewModel.onClaveChange(clave)
    }

    @Test
    fun `con campos invalidos muestra errores y no llama al microservicio`() = runTest {
        escribir("", "123")
        viewModel.onIngresar()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertNotNull(estado.errorAlias)
        assertNotNull(estado.errorClave)
        coVerify(exactly = 0) { repositorio.login(any(), any()) }
    }

    @Test
    fun `al corregir un campo con error se revalida`() {
        viewModel.onIngresar() // marca errores en ambos campos
        viewModel.onAliasChange("admin")
        viewModel.onClaveChange("admin123")

        assertNull(viewModel.uiState.value.errorAlias)
        assertNull(viewModel.uiState.value.errorClave)
    }

    @Test
    fun `login correcto guarda el usuario ingresado`() = runTest {
        coEvery { repositorio.login("admin", "admin123") } returns admin
        escribir("admin", "admin123")

        viewModel.onIngresar()
        advanceUntilIdle()

        assertEquals(admin, viewModel.uiState.value.usuarioIngresado)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun `datos incorrectos muestran mensaje general`() = runTest {
        coEvery { repositorio.login(any(), any()) } returns null
        escribir("admin", "otraclave")

        viewModel.onIngresar()
        advanceUntilIdle()

        assertEquals("Alias o contraseña incorrectos", viewModel.uiState.value.errorGeneral)
        assertNull(viewModel.uiState.value.usuarioIngresado)
    }

    @Test
    fun `un estudiante no puede entrar con contrasena`() = runTest {
        coEvery { repositorio.login(any(), any()) } returns estudiante
        escribir("estudiante1", "clave123")

        viewModel.onIngresar()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.usuarioIngresado)
        assertNotNull(viewModel.uiState.value.errorGeneral)
    }

    @Test
    fun `sin conexion muestra el aviso de microservicios apagados`() = runTest {
        coEvery { repositorio.login(any(), any()) } throws IOException()
        escribir("admin", "admin123")

        viewModel.onIngresar()
        advanceUntilIdle()

        assertEquals(MENSAJE_SIN_CONEXION, viewModel.uiState.value.errorGeneral)
        assertFalse(viewModel.uiState.value.cargando)
    }
}
