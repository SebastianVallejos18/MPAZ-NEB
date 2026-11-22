package cl.duoc.mpazneb.data.remote.api

import cl.duoc.mpazneb.data.remote.dto.PokemonDto
import retrofit2.http.GET
import retrofit2.http.Path

// API externa pública: https://pokeapi.co (no necesita clave)
interface PokeApiService {

    // GET https://pokeapi.co/api/v2/pokemon/25
    @GET("pokemon/{id}")
    suspend fun obtenerPokemon(@Path("id") id: Int): PokemonDto
}
