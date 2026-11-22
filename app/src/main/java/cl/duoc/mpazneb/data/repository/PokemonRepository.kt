package cl.duoc.mpazneb.data.repository

import cl.duoc.mpazneb.data.remote.aModelo
import cl.duoc.mpazneb.data.remote.api.PokeApiService
import cl.duoc.mpazneb.model.Pokemon
import kotlin.random.Random

// Consume la API externa PokeAPI. Es independiente de los microservicios propios:
// no guarda nada en ellos ni modifica los datos de la app.
class PokemonRepository(
    private val api: PokeApiService,
    // Se recibe como parámetro para poder fijar el número en las pruebas unitarias
    private val elegirNumero: () -> Int = { Random.nextInt(1, POKEMON_MAXIMO + 1) }
) {

    suspend fun obtenerPremio(): Pokemon = api.obtenerPokemon(elegirNumero()).aModelo()

    companion object {
        // Primera generación: 1 a 151
        const val POKEMON_MAXIMO = 151
    }
}
