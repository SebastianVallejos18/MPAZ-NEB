package cl.duoc.mpazneb.data.remote.api

import cl.duoc.mpazneb.data.remote.dto.PokemonDto
import retrofit2.http.GET
import retrofit2.http.Path

// PokeAPI: https://pokeapi.co
interface PokeApiService {

    // GET https://pokeapi.co/api/v2/pokemon/25
    @GET("pokemon/{id}")
    suspend fun obtenerPokemon(@Path("id") id: Int): PokemonDto
}
