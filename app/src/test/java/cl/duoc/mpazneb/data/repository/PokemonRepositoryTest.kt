package cl.duoc.mpazneb.data.repository

import cl.duoc.mpazneb.data.remote.api.PokeApiService
import cl.duoc.mpazneb.data.remote.dto.PokemonDto
import cl.duoc.mpazneb.data.remote.dto.SpritesDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PokemonRepositoryTest {

    private val api = mockk<PokeApiService>()

    @Test
    fun `pide a la PokeAPI el numero elegido`() = runTest {
        coEvery { api.obtenerPokemon(25) } returns PokemonDto(
            id = 25, name = "pikachu", sprites = SpritesDto(frontDefault = "https://sprite/25.png")
        )
        // Se fija el número para que la prueba sea predecible
        val repositorio = PokemonRepository(api) { 25 }

        val premio = repositorio.obtenerPremio()

        assertEquals("Pikachu", premio.nombre)
        coVerify { api.obtenerPokemon(25) }
    }

    @Test
    fun `por defecto elige un pokemon de la primera generacion`() = runTest {
        val pedidos = mutableListOf<Int>()
        coEvery { api.obtenerPokemon(any()) } answers {
            pedidos += firstArg<Int>()
            PokemonDto(id = firstArg(), name = "x")
        }
        val repositorio = PokemonRepository(api)

        repeat(20) { repositorio.obtenerPremio() }

        assertTrue(pedidos.all { it in 1..PokemonRepository.POKEMON_MAXIMO })
    }
}
