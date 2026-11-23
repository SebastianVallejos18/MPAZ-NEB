package cl.duoc.mpazneb.model

// Premio que se muestra al estudiante. Viene de PokeAPI.
data class Pokemon(
    val id: Int,
    val nombre: String,
    val imagenUrl: String,
    val tipos: List<String>
)
