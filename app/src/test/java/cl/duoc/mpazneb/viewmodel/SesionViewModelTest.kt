package cl.duoc.mpazneb.viewmodel

import cl.duoc.mpazneb.asistente
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SesionViewModelTest {

    @Test
    fun `iniciar y cerrar sesion`() {
        val viewModel = SesionViewModel()
        assertNull(viewModel.usuarioActual.value)

        viewModel.iniciarSesion(asistente)
        assertEquals(asistente, viewModel.usuarioActual.value)

        viewModel.cerrarSesion()
        assertNull(viewModel.usuarioActual.value)
    }
}
