package cl.duoc.mpazneb.data.remote.dto

import com.google.gson.annotations.SerializedName

// Solo se toman los campos de la PokeAPI que la app necesita; Gson ignora el resto del JSON.
// Ejemplo: https://pokeapi.co/api/v2/pokemon/25
data class PokemonDto(
    val id: Int = 0,
    val name: String = "",
    val sprites: SpritesDto = SpritesDto(),
    val types: List<TipoSlotDto> = emptyList()
)

data class SpritesDto(
    @SerializedName("front_default") val frontDefault: String? = null,
    val other: OtrosSpritesDto? = null
)

data class OtrosSpritesDto(
    @SerializedName("official-artwork") val officialArtwork: ArteOficialDto? = null
)

data class ArteOficialDto(
    @SerializedName("front_default") val frontDefault: String? = null
)

data class TipoSlotDto(
    val slot: Int = 0,
    val type: TipoDto = TipoDto()
)

data class TipoDto(
    val name: String = ""
)
