package cl.duoc.mpazneb.data.remote

import cl.duoc.mpazneb.errorHttp
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.HttpException

class LlamadasTest {

    @Test
    fun `devuelve el resultado si la llamada funciona`() = runTest {
        assertEquals("ok", nuloSiNoExiste { "ok" })
    }

    @Test
    fun `un 404 se convierte en null`() = runTest {
        assertNull(nuloSiNoExiste<String> { throw errorHttp(404) })
    }

    @Test(expected = HttpException::class)
    fun `otros errores http se relanzan`() = runTest {
        nuloSiNoExiste<String> { throw errorHttp(500) }
    }
}
